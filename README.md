<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [WXWeChatToolkits](#wxwechattoolkits)
- [公众号管理](#%E5%85%AC%E4%BC%97%E5%8F%B7%E7%AE%A1%E7%90%86)
  - [用户鉴权](#%E7%94%A8%E6%88%B7%E9%89%B4%E6%9D%83)
  - [JSSDK](#jssdk)
- [微信支付](#%E5%BE%AE%E4%BF%A1%E6%94%AF%E4%BB%98)
  - [统一下单获取预支付代码](#%E7%BB%9F%E4%B8%80%E4%B8%8B%E5%8D%95%E8%8E%B7%E5%8F%96%E9%A2%84%E6%94%AF%E4%BB%98%E4%BB%A3%E7%A0%81)
  - [微信内H5支付](#%E5%BE%AE%E4%BF%A1%E5%86%85h5%E6%94%AF%E4%BB%98)
  - [支付结果回调](#%E6%94%AF%E4%BB%98%E7%BB%93%E6%9E%9C%E5%9B%9E%E8%B0%83)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# WXWeChatToolkits
我的微信SDK，包括公众平台管理、微信支付等各个版本。老实说,微信的文档并不是很友好,坑不少啊~~ 笔者在这里准备的算是半自动化的,自认为的特性有:

- 前后端分离,这里的JS代码和后端代码是可以单独部署的。换言之,微信里需要的各种各样的域名配置与审核,你只要保证你的HTML页面在那个域名下就好,业务逻辑的代码随便放
- 后端这边笔者自己开发时候用的是Spring Boot,但是这里移除了所有Spring Boot的紧密耦合代码,只是用Pure Java API进行实现,也方便单元测试
- 前端这边用的是ES6 + Webpack,可以参考笔者其他前端项目

下面就大概描述下开发流程和可以用到的本代码集的东东,半成品,权当一乐。


# 公众号管理

## 用户鉴权

用户鉴权首先需要检测是否进行认证跳转:

```
WXService wxService = new WXService();


//判断是否为微信系统内
if (wxService.isWeixinBrowser()) {

    //如果是在微信浏览器内判断是否需要进行登录操作
    const openid = wxService.getOpenidOrRedirectToAuth("eapply");

    if (!openid) {
        //如果openid不存在,则提示错误,并且跳转登录授权
        message.info("未登录,现进行登录授权!");
    } else {
        //执行JSSDK的注册
        wxService.jssdkConfig();
    }

}

```

这里的跳转大概是这样的路径:
```
const auth_url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${this.appid}&response_type=code&scope=snsapi_base&state=${state}&redirect_uri=${this.redirect_uri}#wechat_redirect`
```
注意,一开始笔者自己是想将回调之后的跳转路径放到State里面的,但是微信好像对State做了限制,因此在这里只是配置了一个标识,而具体的标识与跳转地址映射写到了后台代码里:

```MPAPI
    /**
     * @return
     * @function 生成State与跳转的网页的配置
     */
    private Map<String, String> stateMapGenerator() {

        Map<String, String> stateMap = new HashMap<>();

        //电子报名页面默认的跳转
        stateMap.put("eapply", "http://mp.dragon.live-forest.com/pay/index.html");

        return stateMap;

    }
```

## JSSDK
微信的官方文档里提到,要申请JSSDK要先去获取jsapi_ticket,而jsapi_ticket需要用access_token换取。要注意,微信的access_token是分类型的,上面用户鉴权也用到了access_token,不过那个的类型是authentic。
这里的access_token类型是jsapi。配置的前端代码是:
```
jssdkConfig() {

        //插入JSSDK脚本
        // load a single file
        loadjs('http://res.wx.qq.com/open/js/jweixin-1.0.0.js', () => {
            // foo.js loaded'//从URL中获取JSSDK信息

            //访问远端获取JSSDK配置信息
            this.getWithQueryParams({
                path: "/mp/jssdk",
                requestData: {
                    url: location.href
                }
            }).then((jssdk)=> {

                console.log(jssdk);

                //配置JSSDK
                wx.config({

                    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。

                    appId: this.appid, // 必填，公众号的唯一标识

                    timestamp: jssdk.timestamp, // 必填，生成签名的时间戳

                    nonceStr: jssdk.noncestr, // 必填，生成签名的随机串

                    signature: jssdk.signature,// 必填，签名，见附录1

                    jsApiList: ['chooseWXPay'] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2

                });

                //监控错误信息
                wx.error(function (res) {

                    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
                    alert(JSON.stringify(res));
                });

            });


        });


    }
```

后端代码是:
```
        //初始化服务
        MPService mpService = new MPService();

        //首先获取接口调用凭据
        String accessToken = mpService.fetchAccessToken4ClientCredential().get("access_token");

        //根据accessToken获取Ticket
        //从微信服务端获取到Ticket
        String apiTicket = mpService.fetchTicketByAccessToken(accessToken, "jsapi").get("ticket");

        //获取随机字符串
        String nonceStr = StringGenerator.getRandomString(10);

        //获取当前时间戳
        Long timeStamp = Instant.now().getEpochSecond();

        //进行签名
        Map<String, Object> signatureMap = new HashMap<>();

        signatureMap.put("noncestr", nonceStr);

        signatureMap.put("jsapi_ticket", apiTicket);

        signatureMap.put("timestamp", timeStamp);

        signatureMap.put("url", url);

        signatureMap.put("signature", Signature.getSign4MP(signatureMap));

        return signatureMap;
```

# 微信支付

注意，微信支付内也需要签名，但是签名的算法以及所需要的Key与公众号管理内还是有一定区别的。

## 统一下单获取预支付代码
前端代码:
```
    fetchPrepayId({
        body = "商品详情",
        out_trade_no = "1415659990",
        total_fee = 1,
        openid = undefined,
        attach //附加信息
    }) {

        if (openid) {
            //如果存在openid,则是以JSAPI方式调用
            return this.getWithQueryParams({
                path: this.fetchPrepayIdUrl,
                requestData: {
                    body,
                    out_trade_no,
                    total_fee,
                    openid,
                    attach
                }
            });
        } else {
            //否则是以APP方式调用
            return this.getWithQueryParams({
                path: this.fetchPrepayIdUrl,
                requestData: {
                    body,
                    out_trade_no,
                    total_fee,
                    attach
                }
            });

        }


    }
```
后端代码:
```
    @SneakyThrows
    public Map<String, Object> prepay(
            String body,
            String out_trade_no,
            Integer total_fee,
            String openid,
            String attach) {

        //最终返回的结果
        Map<String, Object> result = new HashMap<>();

        //调用统一下单服务
        UnifiedOrderService unifiedOrderService = UnifiedOrderService
                .builder(body, out_trade_no, total_fee, getIp("127.0.0.1"))
                .attach(attach)
                .build();

        //获取的返回的同一订单信息
        Map<String, Object> unidiedOrder;

        //判断openid是否存在
        if (openid != null) {
            //如果opendid存在,则创建JSAPI Order
            unidiedOrder = unifiedOrderService.jsApiOrder(openid);
        } else {
            unidiedOrder = unifiedOrderService.appOrder();
        }

//        System.out.println(unidiedOrder);

        /* 最终客户端要提交给微信服务器的订单,因此我们也要将关键信息加进去
        "appId" : "wx2421b1c4370ec43b",     //公众号名称，由商户传入
        "timeStamp":" 1395712654",         //时间戳，自1970年以来的秒数
        "nonceStr" : "e61463f8efa94090b1f366cccfbbb444", //随机串
        "package" : "prepay_id=u802345jgfjsdfgsdg888",
        "signType" : "MD5",         //微信签名方式:
        "paySign" : "70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名
        */

        //获取随机字符串
        String nonceStr = RandomStringGenerator.getRandomStringByLength(20);


        //返回商户对应的AppID
        result.put("appId", Configure.appID);

        result.put("timeStamp", Instant.now().getEpochSecond());

        result.put("nonceStr", nonceStr);

        result.put("package", "prepay_id=" + unidiedOrder.get("prepay_id"));

        result.put("signType", "MD5");

        result.put("paySign", Signature.getSign4Pay(result));

        //直接返回
        return result;
    }
```

## 微信内H5支付

```
    doSyncPay({
        appId="wx7d0444df2763bf91",
        timeStamp="1465698294",
        nonceStr="2g1w8kvb5lamqwfx6j8o",
        package_r="prepay_id=wx2016061210245447b57ae3b30364645260",
        signType="MD5",
        paySign="01B98B973451A1AA83EC062F2F46AB75"
    }, cb) {

        //调用微信支付的接口
        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', {
                "appId": appId,
                "timeStamp": timeStamp,
                "nonceStr": nonceStr,
                "package": package_r,
                "signType": signType,
                "paySign": paySign
            },
            function (res) {

                //打印支付信息
                console.log(res);

                if (res.err_msg == "get_brand_wcpay_request:ok") {
                    //支付成功
                    cb(res);

                } else {

                    // alert(JSON.stringify(res));

                    alert("您取消了支付!");
                }
            }
        )
        ;
    }
```
## 支付结果回调

```
   public String wx_notify(String body) {

//        System.out.println(body);

        PayedOrderService payedOrderService = new PayedOrderService();

        //解析数据
        Map<String, String> parsedMap = payedOrderService.parseNotifyXML(body);

        System.out.println(parsedMap);

        //调用更新状态的函数
        payedOrderService.updateOrderState(parsedMap.get("attach"), "1", "1", parsedMap.get("TransactionId"));

        return "success";
    }
```
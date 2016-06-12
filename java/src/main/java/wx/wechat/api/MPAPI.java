package wx.wechat.api;

import lombok.Getter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import wx.ds.string.StringGenerator;
import wx.wechat.common.Configure;
import wx.wechat.common.signature.SHA1;
import wx.wechat.common.signature.Signature;
import wx.wechat.service.mp.MPService;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 16/6/10.
 */
public class MPAPI extends API {

    @Getter(lazy = true)
    final private Map<String, String> stateMap = stateMapGenerator();

    /**
     * @param signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return
     * @function 验证消息的确来自微信服务器
     */
    public String portal(
            String signature,
            String timestamp,
            String nonce,
            String echostr) {

        String[] str = {Configure.appToken, timestamp, nonce};

        Arrays.sort(str); // 字典序排序

        String bigStr = str[0] + str[1] + str[2];

        // SHA1加密
        String digest = SHA1.encode(bigStr).toLowerCase();

        // 确认请求来至微信
        if (digest.equals(signature)) {
            return echostr;
        } else {
            return "Invalid Signature";
        }
    }

    /**
     * @param code  回调带入的编码
     * @param state 回调带入的状态
     * @return
     * @function 返回授权之后的用户信息
     * @Test 授权的测试地址
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx7d0444df2763bf91&redirect_uri=http%3a%2f%2fmp.dragon.live-forest.com%2fmp%2fauth&response_type=code&scope=snsapi_base&state=eapply
     */
    public ModelAndView auth(
            String code, String state
    ) {

        if (code == null || state == null) {
            //如果传入参数为空,则默认跳转到空页面
            return new ModelAndView("redirect:" + "http://baidu.com");
        }

        //初始化MPService
        MPService mpService = new MPService();

        /**
         * @Step 1 通过code换取网页授权access_token,注意,这个access_token仅用于用户信息获取与认证
         */
        Map<String, String> accessTokenMap = mpService.fetchAccessTokenByCode4Authorization(code);

        //提取出openid
        String openid = accessTokenMap.get("openid");

        //配置要跳转的URL
        String redirectUrl = this.stateMapGenerator().get(state) + "?openid=" + openid;

        //执行跳转操作
        return new ModelAndView("redirect:" + redirectUrl);

    }

    /**
     * @param url 本地的URL
     * @return
     * @function 获取JSSDK所需要的配置信息
     */
    public Map<String, Object> jssdk(
            String url
    ) {

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

    }


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

}

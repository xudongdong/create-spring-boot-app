/**
 * Created by apple on 16/6/7.
 */
require('es6-promise').polyfill();
require('isomorphic-fetch');

import Model from "./model";

/**
 * @function 用于微信支付类
 */
export default class WXPay extends Model {

    /**
     * @function 默认的微信支付类构造器
     */
    constructor() {

        super();
        //预付费的API
        this.fetchPrepayIdUrl = "/mp/prepay";

    }

    /**
     * @function 从远端获取预付费ID
     */
    fetchPrepayId({
        body = "商品详情",
        out_trade_no = "1415659990",
        total_fee = 1,
        openid = undefined
    }) {
        if (openid) {
            //如果存在openid,则是以JSAPI方式调用
            this.get({
                url: this.fetchPrepayIdUrl,
                requestData: {
                    body,
                    out_trade_no,
                    total_fee,
                    openid
                }
            });
        } else {
            //否则是以APP方式调用
            this.get({
                url: this.fetchPrepayIdUrl,
                requestData: {
                    body,
                    out_trade_no,
                    total_fee
                }
            });

        }


    }


    /**
     * @function 同步付款
     * @param appId
     * @param timeStamp
     * @param nonceStr
     * @param package
     * @param signType
     * @param paySign
     */
    doSyncPay({
        appId="wx9b17162ad8941a7c",
        timeStamp="1465379322",
        nonceStr="6x5zpnoohrkl6vffsasg",
        package_r="prepay_id=wx2016060817484234f56bb6df0899433128",
        signType="MD5",
        paySign="4470184DC86AEFBE38E300E2B6A19575"
    }) {

        WeixinJSBridge.invoke(
            'getBrandWCPayRequest', {
                appId,
                timeStamp,
                nonceStr,
                "package":package_r,
                signType,
                paySign
            },
            function (res) {

                console.log(res);

                alert(res.return_msg);

                if (res.err_msg == "get_brand_wcpay_request:ok") {
                }     // 使用以上方式判断前端返回,微信团队郑重提示:res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
            }
        )
        ;
    }


}
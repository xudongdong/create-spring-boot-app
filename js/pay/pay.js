/**
 * Created by apple on 16/6/10.
 */
import WXPay from "../service/wxpay";

const wxPay = new WXPay();

/**
 * @function 微信准备好之后的回调
 */
function onBridgeReady() {

    //首先调用后台获取预付费ID
    wxPay.fetchPrepayId(

    ).then(
        //执行同步付款
        wxPay.doSyncPay
    ).then(
        (result)=> {

        }
    ).catch((err)=> {

    });


}

if (typeof WeixinJSBridge == "undefined") {
    if (document.addEventListener) {
        document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
    } else if (document.attachEvent) {
        document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
        document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
    }
} else {
    onBridgeReady();
}
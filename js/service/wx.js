/**
 * Created by apple on 16/6/10.
 */
import Model from "./model";
/**
 * @function 默认的微信工具类
 */
export default class WXService extends Model {

    constructor() {

        //调用默认构造函数
        super();

        this.appid = "wx7d0444df2763bf91";

        this.redirect_uri = "http%3a%2f%2fmp.dragon.live-forest.com%2fmp%2fauth";

        this.storageKey = {
            openid: "wechat_openid"
        }
    }

    /**
     * @function 判断当前是否在微信内
     * @returns {boolean} true 微信内 false 其他浏览器或者运行环境
     */
    isWeixinBrowser() {
        return /micromessenger/.test(navigator.userAgent.toLowerCase())
    }

    /**
     * @function 获取用户的openid或者根据state进行跳转
     * @param state
     * @returns {*}
     */
    getOpenidOrRedirectToAuth(state) {

        //首先从URL中获取
        if (this.getParameterByName("openid")) {

            //将从URL中获取的openid存入到localStorage中
            localStorage.setItem(this.storageKey.openid, this.getParameterByName("openid"));

            //如果URL中存在着openid
            return this.getParameterByName("openid");
        }

        //否则从本地的localStorage中获取
        if (localStorage.getItem(this.storageKey.openid)) {
            //如果本地localStorage存在openid,则直接返回
            return localStorage.getItem(this.storageKey.openid);
        }

        //否则执行跳转
        const auth_url = `https://open.weixin.qq.com/connect/oauth2/authorize?appid=${this.appid}&response_type=code&scope=snsapi_base&state=${state}&redirect_uri=${this.redirect_uri}#wechat_redirect`

        //进行跳转
        location.href = auth_url;
    }

}
package wx.wechat.api.mp;

import lombok.Getter;
import wx.wechat.api.API;
import wx.wechat.common.Configure;
import wx.wechat.common.SHA1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 16/6/8.
 */

/**
 * @function 负责完成授权过程的API
 */
public class AuthAPI extends API {

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
    public String portal(String signature, String timestamp, String nonce, String echostr) {

        String[] str = {Configure.appToken, timestamp, nonce};

        Arrays.sort(str); // 字典序排序

        String bigStr = str[0] + str[1] + str[2];

        // SHA1加密
        String digest = SHA1.encode(bigStr).toLowerCase();

        // 确认请求来至微信
        if (digest.equals(signature)) {
            return echostr;
        } else {
            return "";
        }
    }

    /**
     * @param code  回调带入的编码
     * @param state 回调带入的状态
     * @return
     * @function 返回授权之后的用户信息
     */
    public Map<String, String> auth(String code, String state) {


        //Step 1 通过code换取网页授权access_token


        return null;
    }

    /**
     * @return
     * @function 生成State与跳转的网页的配置
     */
    private Map<String, String> stateMapGenerator() {

        Map<String, String> stateMap = new HashMap<>();

        //电子报名页面默认的跳转
        stateMap.put("eapply", "");

        return stateMap;

    }

}



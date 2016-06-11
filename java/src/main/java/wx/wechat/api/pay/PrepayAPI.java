package wx.wechat.api.pay;

/**
 * Created by apple on 16/6/8.
 */

import lombok.SneakyThrows;
import lombok.val;
import wx.wechat.api.API;
import wx.wechat.common.Configure;
import wx.wechat.common.RandomStringGenerator;
import wx.wechat.common.Signature;
import wx.wechat.service.pay.UnifiedOrderService;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * @function 模拟PrepayAPI的功能
 */
public class PrepayAPI extends API {

    @SneakyThrows
    public Map<String, String> prepay(String body, String out_trade_no, Integer total_fee) {

        //最终返回的结果
        Map<String, String> result = new HashMap<>();

        //调用统一下单服务
        UnifiedOrderService unifiedOrderService = UnifiedOrderService
                .builder("测试商品", "1415659990", 1, this.getIp())
                .build();


        val unidiedOrder = unifiedOrderService.appOrder();

        /* 最终客户端要提交给微信服务器的订单,因此我们也要将关键信息加进去
        "appId" : "wx2421b1c4370ec43b",     //公众号名称，由商户传入
        "timeStamp":" 1395712654",         //时间戳，自1970年以来的秒数
        "nonceStr" : "e61463f8efa94090b1f366cccfbbb444", //随机串
        "package" : "prepay_id=u802345jgfjsdfgsdg888",
        "signType" : "MD5",         //微信签名方式:
        "paySign" : "70EA570631E4BB79628FBCA90534C63FF7FADD89" //微信签名

        */

        String nonceStr = RandomStringGenerator.getRandomStringByLength(20);


        result.put("appId", Configure.appID);

        result.put("timeStamp", Instant.now().getEpochSecond() + "");

        result.put("nonceStr", nonceStr);

        result.put("package", "prepay_id=" + unidiedOrder.get("prepay_id"));

        result.put("signType", "MD5");

        result.put("paySign", Signature.getSign(result));

        //直接返回
        return result;
    }

}

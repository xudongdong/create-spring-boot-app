package wx.wechat.common;

import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Test;

import java.util.Map;

/**
 * Created by apple on 16/6/5.
 */
public class XMLParser {

    @SneakyThrows
    public static Map<String, Object> getMapFromXML(String responseString) {

        Document document = DocumentHelper.parseText(responseString);

        return null;


    }

    @Test
    @SneakyThrows
    public void test_getMapFromXML() {
        String xml = "<xml><appid><![CDATA[wx7d0444df2763bf91]]></appid>\n" +
                "<bank_type><![CDATA[ICBC_DEBIT]]></bank_type>\n" +
                "<cash_fee><![CDATA[1]]></cash_fee>\n" +
                "<device_info><![CDATA[WEB]]></device_info>\n" +
                "<fee_type><![CDATA[CNY]]></fee_type>\n" +
                "<is_subscribe><![CDATA[Y]]></is_subscribe>\n" +
                "<mch_id><![CDATA[1243378802]]></mch_id>\n" +
                "<nonce_str><![CDATA[0gfe5twrncdh9bvjs5yq]]></nonce_str>\n" +
                "<openid><![CDATA[ormKXjjvAcg8Dpo_TjKVzrmUFTD8]]></openid>\n" +
                "<out_trade_no><![CDATA[DB20160612155510337]]></out_trade_no>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<sign><![CDATA[4CEA670F1D2865C95BB9F86FBBAAA7ED]]></sign>\n" +
                "<time_end><![CDATA[20160612155515]]></time_end>\n" +
                "<total_fee>1</total_fee>\n" +
                "<trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "<transaction_id><![CDATA[4009292001201606127153809026]]></transaction_id>\n" +
                "</xml>\n";

        Document document = DocumentHelper.parseText(xml);

        System.out.println(document.getRootElement().element("appid").getText());

    }
}

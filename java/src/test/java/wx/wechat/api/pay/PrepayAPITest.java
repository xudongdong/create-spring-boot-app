package wx.wechat.api.pay;

import org.junit.Before;
import org.junit.Test;
import wx.wechat.api.pay.PrepayAPI;

/**
 * Created by apple on 16/6/8.
 */
public class PrepayAPITest {

    PrepayAPI prepayAPI;

    @Before
    public void setUp() {

        prepayAPI = new PrepayAPI();

    }

    @Test
    public void test_prepay() {

        System.out.println(prepayAPI.prepay("测试商品", "1415659990", 1));
    }

}

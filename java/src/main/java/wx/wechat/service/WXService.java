package wx.wechat.service;

/**
 * Created by apple on 16/6/7.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.*;
import lombok.SneakyThrows;
import wx.wechat.common.Signature;
import wx.wechat.utils.XMLUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @function 封装一些常用的方法
 */
public class WXService {


    public String API_WEIXIN_HOST = "api.weixin.qq.com";

    private final OkHttpClient client = new OkHttpClient();

    private static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/xml; charset=utf-8");


    /**
     * @param host
     * @param path
     * @param params 参数对,必须是双数
     * @return
     */
    @SneakyThrows
    protected Map<String, String> getWithParams(String host, String path, Map<String, String> params) {

        //预设的返回的数据集
        Map<String, String> result = new HashMap<>();

        //构建请求路径
        HttpUrl.Builder httpUrlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment(path);

        //添加查询参数
        params.forEach((k, v) -> {
            httpUrlBuilder.addQueryParameter(k, v);
        });

        //构建请求
        Request request = new Request.Builder()
                .url(httpUrlBuilder.build())
                .get()
                .build();

        //发起请求
        Response response = client.newCall(request).execute();

        //解析获取的数据并且返回Map
        JSONObject.parseObject(response.body().string()).forEach((k, v) -> {
            result.put(k, String.valueOf(v));
        });

        return result;
    }

    /**
     * @param url         待请求的URL的地址
     * @param requestData 请求的数据
     * @return
     * @function 根据输入的数据向URL发起请求, 并且进行自动的签名
     */
    @SneakyThrows
    protected Map<String, Object> postByXML(String url, Map requestData) {

        //首先获取签名
        String signature = Signature.getSign(requestData);

        //签名添加到请求数据中
        requestData.put("sign", signature);

        //封装成XML数据
        String requestXML = XMLUtils.convertToXML(requestData);

        //调用okHttp发起请求
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, requestXML))
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return XMLUtils.XML2Map(response.body().string());
    }


}

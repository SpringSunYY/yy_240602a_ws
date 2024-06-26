package com.yy.manage.utils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;

/**
 * @Project: WS
 * @Package: com.yy.manage.utils
 * @Author: YY
 * @CreateTime: 2024-06-14  17:26
 * @Description: HttpUtil
 * @Version: 1.0
 */
public class HttpGetUtil  extends HttpEntityEnclosingRequestBase {
    private final static String METHOD_NAME = "GET";

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    public HttpGetUtil() {
        super();
    }

    public HttpGetUtil(final URI uri) {
        super();
        setURI(uri);
    }
    HttpGetUtil(final String uri) {
        super();
        setURI(URI.create(uri));
    }


  /**
   * @description:
   * @author: YY
   * @method: sendJsonByGetReq
   * @date: 2024/6/14 17:40
   * @param:
   * @param: url
   * @param: param 参数body
   * @param: encoding
   * @return: java.lang.String
   **/
    public static String sendJsonByGetReq(String url, String param, String encoding) throws Exception {
        String body = "";
        //创建httpclient对象
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGetUtil httpGetWithEntity = new HttpGetUtil(url);
        HttpEntity httpEntity = new StringEntity(param, ContentType.APPLICATION_JSON);
        httpGetWithEntity.setEntity(httpEntity);
        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpGetWithEntity);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            //按指定编码转换结果实体为String类型
            body = EntityUtils.toString(entity, encoding);
        }
        //释放链接
        response.close();
        return body;
    }

}

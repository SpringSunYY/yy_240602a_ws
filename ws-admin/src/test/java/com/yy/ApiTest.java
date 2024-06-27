package com.yy;

import com.alibaba.fastjson2.JSON;
import com.yy.common.core.redis.RedisCache;
import com.yy.common.exception.ServiceException;
import com.yy.common.utils.StringUtils;
import com.yy.manage.domain.WsOrderInfo;
import com.yy.manage.domain.vo.ReturnTaskVo;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yy.common.constant.ConfigConstants.*;
import static com.yy.manage.utils.HttpGetUtil.sendJsonByGetReq;

/**
 * @Project: WS
 * @Package: com.yy
 * @Author: YY
 * @CreateTime: 2024-06-12  22:36
 * @Description: ApiTest
 * @Version: 1.0
 */
@SpringBootTest
public class ApiTest {

    @Autowired
    private RedisCache redisCache;


    @Test
    public void updateStatus(){
        //获取基本参数
        String taskUrl = redisCache.getCacheConfig(WS_TASK_UPDATE_STATUS_API);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);


        WsOrderInfo wsOrderInfo = new WsOrderInfo();
        wsOrderInfo.setOutPutFilePath("");
        wsOrderInfo.setTaskId("96394");
        wsOrderInfo.setName("测试修改AAAA");
        wsOrderInfo.setCopyContent("测试你好{url}");
        wsOrderInfo.setLinkContent("www.baidu.com");
        wsOrderInfo.setSendTime(new Date());
        wsOrderInfo.setStatus("3");

        //创建一个默认的http客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

            taskBuilder.addPart("send_time", new StringBody(String.valueOf(wsOrderInfo.getSendTime().getTime() / 1000), StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("new_status", new StringBody(wsOrderInfo.getStatus() != null ? wsOrderInfo.getStatus() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("task_id", new StringBody(wsOrderInfo.getTaskId() != null ? wsOrderInfo.getTaskId() : "", StandardCharsets.UTF_8));

            HttpEntity taskEntry = taskBuilder.build();
            taskRequest.setEntity(taskEntry);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);

            HttpEntity taskResponseEntity = taskResponse.getEntity();

            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
                System.err.println("taskResult = " + taskResult);
            }
        } catch (Exception e) {
//            wsOrderInfoMapper.deleteWsOrderInfoById(wsOrderInfo.getId());
            System.err.println("e = " + e.getMessage());
            throw new ServiceException("更新订单发送时间失败！！！");
        }
    }
    @Test
    public void updateSendTime(){
        //获取基本参数
        String taskUrl = redisCache.getCacheConfig(WS_TASK_UPDATE_SENDTIME_API);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);


        WsOrderInfo wsOrderInfo = new WsOrderInfo();
        wsOrderInfo.setOutPutFilePath("");
        wsOrderInfo.setTaskId("96514");
        wsOrderInfo.setName("测试修改AAAA");
        wsOrderInfo.setCopyContent("测试你好{url}");
        wsOrderInfo.setLinkContent("www.baidu.com");
        wsOrderInfo.setSendTime(new Date());

        //创建一个默认的http客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

            taskBuilder.addPart("send_time", new StringBody(String.valueOf(wsOrderInfo.getSendTime().getTime() / 1000), StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("task_id", new StringBody(wsOrderInfo.getTaskId() != null ? wsOrderInfo.getTaskId() : "", StandardCharsets.UTF_8));

            HttpEntity taskEntry = taskBuilder.build();
            taskRequest.setEntity(taskEntry);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);

            HttpEntity taskResponseEntity = taskResponse.getEntity();

            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
                System.err.println("taskResult = " + taskResult);
            }
        } catch (Exception e) {
//            wsOrderInfoMapper.deleteWsOrderInfoById(wsOrderInfo.getId());
            System.err.println("e = " + e.getMessage());
            throw new ServiceException("更新订单发送时间失败！！！");
        }
    }
    @Test
    public void updateOrder() {
        String taskUrl = "https://waapi.qunfa.io/post_edit_task_info";
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);

        WsOrderInfo wsOrderInfo = new WsOrderInfo();
        wsOrderInfo.setOutPutFilePath("");
        wsOrderInfo.setTaskId("93987");
        wsOrderInfo.setName("测试修改AAAA");
        wsOrderInfo.setCopyContent("测试你好{url}");
        wsOrderInfo.setLinkContent("www.baidu.com");

        //创建一个默认的http客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

//            File file = new File(wsOrderInfo.getOutPutFilePath());
//            FileBody fileBody = new FileBody(file);
//            taskBuilder.addPart("file", fileBody);
//
//            System.err.println("wsOrderInfo = " + wsOrderInfo);

            //初始化参数
            taskBuilder.addPart("task_name", new StringBody(wsOrderInfo.getName() != null ? wsOrderInfo.getName() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("text", new StringBody(wsOrderInfo.getCopyContent() != null ? wsOrderInfo.getCopyContent() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("url_list", new StringBody(wsOrderInfo.getLinkContent() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinkContent()) : "", StandardCharsets.UTF_8));
//            taskBuilder.addPart("task_type", new StringBody(wsOrderInfo.getSendType(), StandardCharsets.UTF_8));
//            taskBuilder.addPart("send_time", new StringBody(String.valueOf(wsOrderInfo.getSendTime().getTime() / 1000), StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("check_key", new StringBody(wsOrderInfo.getId() != null ? wsOrderInfo.getId() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("task_id", new StringBody(wsOrderInfo.getTaskId() != null ? wsOrderInfo.getTaskId() : "", StandardCharsets.UTF_8));
            //判断是否为超链
//            if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_2)) {
//
//                taskBuilder.addPart("kefu_list", new StringBody(wsOrderInfo.getServicePhone() != null ? wsOrderInfo.getServicePhone() : "", StandardCharsets.UTF_8));
//            }
//            //判断是否为苹果链
//            if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_3)) {
//                taskBuilder.addPart("b1", new StringBody(wsOrderInfo.getButtons1() != null ? wsOrderInfo.getButtons1() : "", StandardCharsets.UTF_8));
//                taskBuilder.addPart("link1_list", new StringBody(wsOrderInfo.getLinks1() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks1()) : "", StandardCharsets.UTF_8));
//                taskBuilder.addPart("b2", new StringBody(wsOrderInfo.getButtons2() != null ? wsOrderInfo.getButtons2() : "", StandardCharsets.UTF_8));
//                taskBuilder.addPart("link2_list", new StringBody(wsOrderInfo.getLinks2() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks2()) : "", StandardCharsets.UTF_8));
//                taskBuilder.addPart("b3", new StringBody(wsOrderInfo.getButtons3() != null ? wsOrderInfo.getButtons3() : "", StandardCharsets.UTF_8));
//                taskBuilder.addPart("link3_list", new StringBody(wsOrderInfo.getLinks3() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks3()) : "", StandardCharsets.UTF_8));
//            }

            HttpEntity taskEntry = taskBuilder.build();
            taskRequest.setEntity(taskEntry);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);

            HttpEntity taskResponseEntity = taskResponse.getEntity();

            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
                System.out.println("taskResult = " + taskResult);
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void sendOrder() {
        String apiToken = "1gMTLGE8FNFfhY6f0MPPCNJczeQLG5pj";
        String userId = "18";
        String taskUrl = "https://waapi.qunfa.io/post_create_task";
//        String uploadUrl = "http://127.0.0.1:5858/post_create_task";
        String filePath = "D:\\ruoyi\\uploadPath\\upload\\2024\\06\\09\\测试用的巴西国家账号 - 副本_20240609200209A001.txt";
        String taskId = null;

        // 创建一个默认的HTTP客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();

        try {
            // 第一个请求：发送任务信息并获取任务ID
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

            File file = new File(filePath);
            FileBody fileBody = new FileBody(file);
            taskBuilder.addPart("file", fileBody);
            taskBuilder.addPart("task_name", new StringBody("测试-普链任务", StandardCharsets.UTF_8));
            taskBuilder.addPart("text", new StringBody("这是一个测试的普链任务，访问 {url} 获取更多信息。", StandardCharsets.UTF_8));
            taskBuilder.addPart("url_list", new StringBody("[\"https://example.com\",\"https://example.com\"]", StandardCharsets.UTF_8));
            taskBuilder.addPart("task_type", new StringBody("1", StandardCharsets.UTF_8));
            taskBuilder.addPart("send_time", new StringBody("1916137648", StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("task_type", new StringBody("2", StandardCharsets.UTF_8));
            taskBuilder.addPart("kefu_list", new StringBody("[\"5519981943862,5515997164572,5513997314624\"]", StandardCharsets.UTF_8));

            HttpEntity taskEntity = taskBuilder.build();
            taskRequest.setEntity(taskEntity);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);
            try {
                HttpEntity taskResponseEntity = taskResponse.getEntity();
                if (taskResponseEntity != null) {
                    String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
                    System.out.println("Task response: " + taskResult);

                    // 解析响应以获取任务ID
                    JSONObject jsonResponse = new JSONObject(taskResult);
                    taskId = jsonResponse.getString("task_id");  // 假设响应中有task_id字段
                }
            } finally {
                taskResponse.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void getTaskStatus() throws JSONException {
        String apiUrl = "https://waapi.qunfa.io/get_task_info/90601";
        String apiToken = "1gMTLGE8FNFfhY6f0MPPCNJczeQLG5pj";
        String userId = "18";

        // 构建带有JSON数据的查询参数
        String queryParams = buildQueryParams(apiToken, userId);

        // 执行 GET 请求
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            // 创建 GET 请求
            HttpGet request = new HttpGet(apiUrl + queryParams);

            // 执行请求并获取响应
            CloseableHttpResponse response = httpClient.execute(request);

            try {
                // 获取响应状态码
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("响应状态码: " + statusCode);

                // 获取响应实体
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    // 将响应实体转换为字符串
                    String responseBody = EntityUtils.toString(responseEntity);

                    // 打印响应内容
                    System.out.println("响应内容: " + responseBody);

                    // 这里可以根据实际需求进一步处理响应内容
                }
            } finally {
                // 关闭响应
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 辅助方法：构建带有JSON数据的查询参数
    private static String buildQueryParams(String apiToken, String userId) {
        try {
            // 构建JSON对象
            String encodedApiToken = URLEncoder.encode(apiToken, "UTF-8");
            String encodedUserId = URLEncoder.encode(userId, "UTF-8");

            // 构建带有查询参数的完整URL
            return "?api_token=" + encodedApiToken + "&user_id=" + encodedUserId;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @Test
    public void test() {
        String apiUrl = "https://waapi.qunfa.io/get_task_info/96514";
        String apiToken = "1gMTLGE8FNFfhY6f0MPPCNJczeQLG5pj";
        String userId = "18";
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        try {
            String s = sendJsonByGetReq(apiUrl, reqParams, "UTF-8");
            System.out.println("请求Get请求返回结果：" + s);
            ReturnTaskVo returnTaskVo = JSON.parseObject(s, ReturnTaskVo.class);
            System.out.println("returnTaskVo = " + returnTaskVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getFileWithExcel() {
        long start = System.currentTimeMillis();
        System.out.println("开始执行");
        String apiUrl = "https://wa.qunfa.io/files/res_succ_90613.txt";
        String apiToken = "1gMTLGE8FNFfhY6f0MPPCNJczeQLG5pj";
        String userId = "18";
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        String[] succString = new String[]{};
        try {
            String s = sendJsonByGetReq(apiUrl, reqParams, "UTF-8");
            System.out.println(s);
            succString = s.split("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String s : succString) {
            System.out.println("su = " + s);
        }
        apiUrl = "https://waapi.qunfa.io/files/res_fail_90613.txt";
        String[] failString = new String[]{};
        try {
            String s = sendJsonByGetReq(apiUrl, reqParams, "UTF-8");
            // 检查字符串中是否包含非数字字符
            if (!containsNonNumeric(s)) {
                failString = s.split("\n");
            } else {
                System.out.println("响应包含非数字字符，不赋值给failString。");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String discardedPath = "G:\\24\\Java\\yy240602A\\res_succ_90260.txt";
//        ExcelFileUtil.writeExcelFile(succString,discardedPath,null,null,null);
        long end = System.currentTimeMillis();
        System.out.println("end-start = " + (end - start));

    }

    // 检查字符串是否包含非数字字符的函数
    private static boolean containsNonNumeric(String s) {
        Pattern p = Pattern.compile("[^0-9\\n]"); // 正则表达式匹配非数字和非换行符的字符
        Matcher m = p.matcher(s);
        return m.find();
    }

    @Test
    public void getFile() {
        String apiUrl = "https://wa.qunfa.io/files/res_fail_90613.txt";
        String apiToken = "1gMTLGE8FNFfhY6f0MPPCNJczeQLG5pj";
        String userId = "18";
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        String[] succString = null;
        try {
            String s = sendJsonByGetReq(apiUrl, reqParams, "UTF-8");
            System.out.println(s);
            succString = s.split("\n");
            for (String string : succString) {
                System.out.println(string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCreateTask() {
        long start = System.currentTimeMillis();
        System.out.println("开始执行");
        String apiUrl = "https://waapi.qunfa.io/get_new_task_status";
        String apiToken = "1gMTLGE8FNFfhY6f0MPPCNJczeQLG5pj";
        String userId = "18";
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        try {
            String s = sendJsonByGetReq(apiUrl, reqParams, "UTF-8");
            System.out.println(s);
            ReturnTaskVo returnTaskVo = JSON.parseObject(s, ReturnTaskVo.class);
            System.out.println("returnTaskVo = " + returnTaskVo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("end-start = " + (end - start));

    }
}




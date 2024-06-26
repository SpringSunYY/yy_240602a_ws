package com.yy.manage.service.impl;

import com.alibaba.fastjson2.JSON;
import com.yy.common.annotation.DataScope;
import com.yy.common.config.RuoYiConfig;
import com.yy.common.core.domain.entity.SysDept;
import com.yy.common.core.domain.entity.SysUser;
import com.yy.common.core.redis.RedisCache;
import com.yy.common.exception.ServiceException;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.file.FileUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.*;
import com.yy.manage.domain.vo.ReturnTaskVo;
import com.yy.manage.mapper.AgencyCountryInfoMapper;
import com.yy.manage.mapper.AgencyUserInfoMapper;
import com.yy.manage.mapper.WsOrderInfoMapper;
import com.yy.manage.service.*;
import com.yy.manage.utils.ExcelFileUtil;
import com.yy.system.service.ISysDeptService;
import com.yy.system.service.ISysUserService;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yy.common.constant.ApiReturnConstants.RETURN_STATUS_ERR;
import static com.yy.common.constant.ApiReturnConstants.RETURN_STATUS_SUCC;
import static com.yy.common.constant.ConfigConstants.*;
import static com.yy.common.constant.DictDataConstants.*;
import static com.yy.manage.utils.FileUtil.getFileLines;
import static com.yy.manage.utils.HttpGetUtil.sendJsonByGetReq;

/**
 * WS订单信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-13
 */
@Service
public class WsOrderInfoServiceImpl implements IWsOrderInfoService {
    @Autowired
    private WsOrderInfoMapper wsOrderInfoMapper;


    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IAgencyCountryInfoService agencyCountryInfoService;

    @Autowired
    private IAgencyUserInfoService agencyUserInfoService;

    @Autowired
    private IOperationHistoryInfoService historyInfoService;

    @Autowired
    private ICountryInfoService countryInfoService;

    @Autowired
    private IIntegralHistoryInfoService integralHistoryInfoService;

    @Autowired
    private IIntegralInfoService integralInfoService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private AgencyCountryInfoMapper agencyCountryInfoMapper;


    @Autowired
    private AgencyUserInfoMapper agencyUserInfoMapper;


    /**
     * 查询WS订单信息
     *
     * @param id WS订单信息主键
     * @return WS订单信息
     */
    @Override
    public WsOrderInfo selectWsOrderInfoById(String id) {
        return wsOrderInfoMapper.selectWsOrderInfoById(id);
    }

    /**
     * 查询WS订单信息列表
     *
     * @param wsOrderInfo WS订单信息
     * @return WS订单信息
     */
    @DataScope(userAlias = "tb_ws_order_info", deptAlias = "tb_ws_order_info")
    @Override
    public List<WsOrderInfo> selectWsOrderInfoList(WsOrderInfo wsOrderInfo) {
        if (!SecurityUtils.hasPermi("look:is:delete")) {
            wsOrderInfo.setIsDelete(WS_IS_DELETE_0);
        }
        List<WsOrderInfo> wsOrderInfos = wsOrderInfoMapper.selectWsOrderInfoList(wsOrderInfo);
        for (WsOrderInfo info : wsOrderInfos) {
            //判断是否为空并赋值
            SysUser sysUser = userService.selectUserById(info.getUserId());
            if (StringUtils.isNotNull(sysUser)) {
                info.setUserName(sysUser.getUserName());
            }
            SysDept sysDept = deptService.selectDeptById(info.getDeptId());
            if (StringUtils.isNotNull(sysDept)) {
                info.setDeptName(sysDept.getDeptName());
                info.setAgencyUserName(sysDept.getLeader());
            }
            CountryInfo countryInfo = countryInfoService.selectCountryInfoById(info.getCountryId());
            if (StringUtils.isNotNull(countryInfo)) {
                info.setCountryName(countryInfo.getName());
            }
        }
        return wsOrderInfos;
    }

    /**
     * 新增WS订单信息
     *
     * @param wsOrderInfo WS订单信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertWsOrderInfo(WsOrderInfo wsOrderInfo) {
        //判断是否为超链，如果是初始并校验客服号码
        if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_2)) {
            wsOrderInfo.setServicePhone(parseArrayToNumberStringAndVerify(wsOrderInfo.getServicePhone()));
        }

        //过滤文件
        optimizeOrder(wsOrderInfo);
        //1、获取税率 并赋值
        String rateStr = redisCache.getCacheConfig(WS_INTEGRAL_EXCHANGE_RATE);
        if (StringUtils.isNull(rateStr)) {
            throw new ServiceException("请联系管理员设置积分税率！！！");
        }
        BigDecimal rate = new BigDecimal(rateStr); // 假设rateStr是一个有效的数字字符串
        RoundingMode roundingMode = RoundingMode.HALF_UP; // 这里使用四舍五入的舍入模式
        BigDecimal price = wsOrderInfo.getActualIntegral().divide(rate, roundingMode);
        wsOrderInfo.setPrices(price);
//        System.out.println("price = " + price);

        wsOrderInfo.setDeptId(SecurityUtils.getDeptId());
        wsOrderInfo.setUserId(SecurityUtils.getUserId());
        wsOrderInfo.setStatus(WS_TASK_STATUS_1);
        wsOrderInfo.setIsDelete(WS_IS_DELETE_0);
        String id = IdUtils.fastSimpleUUID();
        wsOrderInfo.setCreateTime(DateUtils.getNowDate());

        //查询用户可用积分是否比现在少
        IntegralInfo integralInfo = new IntegralInfo();
        integralInfo.setUserId(wsOrderInfo.getUserId());
        List<IntegralInfo> integralInfos = integralInfoService.selectIntegralInfoList(integralInfo);
        if (StringUtils.isEmpty(integralInfos) || integralInfos.get(0).getIntegral().compareTo(wsOrderInfo.getUseIntegral()) < 0) {
            throw new ServiceException("您的积分不足！！！");
        }
        //如果订单传过来的没有id则添加，有则是重新提交
        if (StringUtils.isNull(wsOrderInfo.getId())) {
            wsOrderInfo.setId(id);
            wsOrderInfo.setCreateTime(DateUtils.getNowDate());
            wsOrderInfo.setStatus(WS_TASK_STATUS_0);
            wsOrderInfoMapper.insertWsOrderInfo(wsOrderInfo);
            System.out.println("插入");
        }
        System.out.println("不插入");
        //创建订单
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            createTask(wsOrderInfo);
        });
        return 1;
    }

    private String parseArrayToNumberStringAndVerify(String string) {
        if (StringUtils.isNull(string)) {
            throw new ServiceException("客服号码未填！！！");
        }
        String[] lines = string.split("\n");
        JSONArray jsonArray = new JSONArray();
        for (String line : lines) {
            if (line.length() < 7) {
                throw new ServiceException("客服号码校验失败，长度不可低于7！！！");
            }
            jsonArray.put(line);
        }
        return jsonArray.toString();
    }


    /**
     * @description: 创建订单
     * @author: YY
     * @method: createTask
     * @date: 2024/6/13 20:49
     * @param:
     * @param: wsOrderInfo
     * @return: com.yy.manage.domain.WsOrderInfo
     **/
    private WsOrderInfo createTask(WsOrderInfo wsOrderInfo) {
        //获取基本参数
        String taskUrl = redisCache.getCacheConfig(WS_CREATE_TASK_URL);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);

        //创建一个默认的http客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

            File file = new File(wsOrderInfo.getOutPutFilePath());
            FileBody fileBody = new FileBody(file);
            taskBuilder.addPart("file", fileBody);

            System.err.println("wsOrderInfo = " + wsOrderInfo);

            //初始化参数
            taskBuilder.addPart("task_name", new StringBody(wsOrderInfo.getName() != null ? wsOrderInfo.getName() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("text", new StringBody(wsOrderInfo.getCopyContent() != null ? wsOrderInfo.getCopyContent() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("url_list", new StringBody(wsOrderInfo.getLinkContent() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinkContent()) : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("task_type", new StringBody(wsOrderInfo.getSendType(), StandardCharsets.UTF_8));
            taskBuilder.addPart("send_time", new StringBody(String.valueOf(wsOrderInfo.getSendTime().getTime() / 1000), StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("check_key", new StringBody(wsOrderInfo.getId() != null ? wsOrderInfo.getId() : "", StandardCharsets.UTF_8));

            //判断是否为超链
            if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_2)) {
                taskBuilder.addPart("kefu_list", new StringBody(wsOrderInfo.getServicePhone() != null ? wsOrderInfo.getServicePhone() : "", StandardCharsets.UTF_8));
            }
            //判断是否为苹果链
            if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_3)) {
                taskBuilder.addPart("b1", new StringBody(wsOrderInfo.getButtons1() != null ? wsOrderInfo.getButtons1() : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("link1_list", new StringBody(wsOrderInfo.getLinks1() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks1()) : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("b2", new StringBody(wsOrderInfo.getButtons2() != null ? wsOrderInfo.getButtons2() : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("link2_list", new StringBody(wsOrderInfo.getLinks2() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks2()) : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("b3", new StringBody(wsOrderInfo.getButtons3() != null ? wsOrderInfo.getButtons3() : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("link3_list", new StringBody(wsOrderInfo.getLinks3() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks3()) : "", StandardCharsets.UTF_8));
            }

            HttpEntity taskEntry = taskBuilder.build();
            taskRequest.setEntity(taskEntry);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);

            HttpEntity taskResponseEntity = taskResponse.getEntity();

            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
                //System.err.println("taskResult = " + taskResult);
                //获取任务id
                // 解析响应以获取任务ID
                JSONObject jsonResponse = new JSONObject(taskResult);
                String taskId = jsonResponse.getString("task_id");  // 假设响应中有task_id字段

                wsOrderInfo.setTaskId(taskId);
                wsOrderInfo.setStatus(WS_TASK_STATUS_1);
                //更新任务
                updateWsOrderInfo(wsOrderInfo);
            }
        } catch (Exception e) {
            //wsOrderInfoMapper.deleteWsOrderInfoById(wsOrderInfo.getId());
            System.err.println("e = " + e.getMessage());
            throw new ServiceException("添加订单失败！！！");
        }

        return wsOrderInfo;
    }

    /**
     * 过滤文件
     *
     * @param orderInfo
     * @return
     */
    private WsOrderInfo optimizeOrder(WsOrderInfo orderInfo) {
        filterPhoneNumbers(orderInfo);

        //获取短信转换率 计算实际积分
        BigDecimal noteExchangeBig = getNoteExchangeBig(orderInfo);
        BigDecimal multiply = noteExchangeBig.multiply(BigDecimal.valueOf(orderInfo.getOrderNumber()));
        //保留两位小数
        multiply = multiply.setScale(2, RoundingMode.UP);
        orderInfo.setActualIntegral(multiply);

        //计算实际价格
        //1、获取税率 并赋值
        String rateStr = redisCache.getCacheConfig(WS_INTEGRAL_EXCHANGE_RATE);
        if (StringUtils.isNull(rateStr)) {
            throw new ServiceException("请联系管理员设置积分税率！！！");
        }
        BigDecimal rate = new BigDecimal(rateStr); // 假设rateStr是一个有效的数字字符串
        RoundingMode roundingMode = RoundingMode.HALF_UP; // 这里使用四舍五入的舍入模式
        BigDecimal price = orderInfo.getActualIntegral().divide(rate, roundingMode);
        orderInfo.setPrices(price);

        orderInfo.setStatus(WS_ORDER_STATUS_2);
//        System.err.println("优化订单 = " + orderInfo);
        return orderInfo;
    }

    /**
     * @description: 获取短信转换 税率以及积分
     * @author: YY
     * @method: getNoteExchangeBig
     * @date: 2024/6/8 15:04
     * @param:
     * @return: java.math.BigDecimal
     **/
    private BigDecimal getNoteExchangeBig(WsOrderInfo orderInfo) {
        //获取短信转换率
        String noteExchange = redisCache.getCacheConfig(WS_NOTE_EXCHANGE_RATE);
        if (StringUtils.isNull(noteExchange)) {
            throw new ServiceException("全局短信转换率为空，请联系管理员！！！");
        }
        BigDecimal noteExchangeBig = BigDecimal.valueOf(Float.parseFloat(noteExchange));

        //从国家里面获取是否有该国家的转换率 查询条件为部门和国家id，只会查到一个
        AgencyCountryInfo agencyCountryInfo = new AgencyCountryInfo();
        agencyCountryInfo.setDeptId(orderInfo.getDeptId());
        agencyCountryInfo.setCountryId(orderInfo.getCountryId());
        List<AgencyCountryInfo> agencyCountryInfos = agencyCountryInfoMapper.selectAgencyCountryInfoList(agencyCountryInfo);
        if (StringUtils.isNotEmpty(agencyCountryInfos)) {
            noteExchangeBig = agencyCountryInfos.get(0).getPrices();
            System.err.println("国家 noteExchangeBig = " + noteExchangeBig);
        }

        //同理，从用户获取转换率 查询条件为用户id和部门只会查到一个
        AgencyUserInfo agencyUserInfo = new AgencyUserInfo();
        agencyUserInfo.setUserId(orderInfo.getUserId());
        agencyUserInfo.setDeptId(orderInfo.getDeptId());
        List<AgencyUserInfo> agencyUserInfos = agencyUserInfoMapper.selectAgencyUserInfoList(agencyUserInfo);
        if (StringUtils.isNotEmpty(agencyUserInfos)) {
            noteExchangeBig = agencyUserInfos.get(0).getPrices();
//            System.err.println("用户 noteExchangeBig = " + noteExchangeBig);
        }
        return noteExchangeBig;
    }

    /**
     * @description: 过滤手机号码
     * @author: YY
     * @method: filterPhoneNumbers
     * @date: 2024/6/8 16:34
     * @param:
     * @param: inputFilePath 输入路径
     * @param: outputFilePath 输出路径
     * @param: prefix 号码前缀
     * @return: void
     **/
    public void filterPhoneNumbers(WsOrderInfo orderInfo) {

        //获取文件路径
        String initFilePath = FileUtils.initFilePath(orderInfo.getFileContent());
        String inputFilePath = RuoYiConfig.getProfile() + initFilePath;
        String fileName = FileUtils.getName(inputFilePath);
        String outputPath = FileUtils.getFilePath(inputFilePath);
        String outputFilePath = outputPath + File.separator + "filter" + File.separator + fileName;
        String discardedFilePath = outputPath + File.separator + "discarded" + File.separator + fileName;
        CountryInfo countryInfo = countryInfoService.selectCountryInfoById(orderInfo.getCountryId());
        String prefix = countryInfo.getPhoneCode();
        //设置输出路径，后续创建订单需要
        orderInfo.setOutPutFilePath(outputFilePath);
        // 创建必要的目录
        createDirectoryIfNotExists(outputFilePath);
        // 创建必要的目录
        createDirectoryIfNotExists(discardedFilePath);

        // 读取输入文件的所有行
        List<String> phoneNumbers = null;
        try {
            phoneNumbers = Files.readAllLines(Paths.get(inputFilePath));
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败");
        }

        // 过滤出以指定前缀开头的号码，并去重
        Set<String> filteredNumbers = phoneNumbers.stream()
                .filter(number -> number.startsWith(prefix))
                .collect(Collectors.toSet());

        // 将过滤后的号码列表转换为数组
        List<String> filteredList = new ArrayList<>(filteredNumbers);

        // 打乱号码列表
        Collections.shuffle(filteredList);

        //获取丢弃比例 行数*比例
        Float exchangeLine = getExchangeLine(orderInfo);


        // 计算需要保留和丢弃的数量
        int totalSize = filteredList.size();
        int retainSize = (int) (totalSize * exchangeLine);

        //从过滤文件获取行数
        Long fileLines = (long) retainSize;

        //获取文件最小行，如果小于最小行不可通过！！！
        String minLine = redisCache.getCacheConfig(WS_NOTE_MIN_LINE);
        try {
            long minLineLong = Long.parseLong(minLine);
            if (fileLines < minLineLong || minLineLong < 0) {
                throw new ServiceException("检测有效文件号码有效行数小于：" + minLine + "行，请检测是否是文件错误还是选择国家错误，修改错误之后请手动检查文件！！！");
            }
        } catch (NumberFormatException e) {
            throw new ServiceException("请联系管理员设置正确的最小行数，应为正整数！！！");
        }

        // 划分保留和丢弃的号码
        List<String> retainedNumbers = filteredList.subList(0, retainSize);
        List<String> discardedNumbers = filteredList.subList(retainSize, totalSize);

        // 将保留的号码写入输出文件
        try {
            BufferedWriter outputWrite = Files.newBufferedWriter(Paths.get(outputFilePath));
            for (String number : retainedNumbers) {
                outputWrite.write(number);
                outputWrite.newLine();
            }
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(discardedFilePath));
            for (String number : discardedNumbers) {
                writer.write(number);
                writer.newLine();
            }
            writer.close();
            outputWrite.close();
        } catch (IOException e) {
            System.out.println("e = " + e);
        }


        String lastFilePath = FileUtils.getFilePath(orderInfo.getFileContent());
        orderInfo.setFileFilter(lastFilePath + File.separator + "filter" + File.separator + fileName);
        orderInfo.setDiscardedFile(lastFilePath + File.separator + "discarded" + File.separator + fileName);
        orderInfo.setOptimizedNumber(orderInfo.getOrderNumber() - fileLines);
        orderInfo.setActualNumber(fileLines);
    }

    /**
     * @description:获取行数比例
     * @author: YY
     * @method: getExchangeLine
     * @date: 2024/6/20 14:47
     * @param:
     * @param: orderInfo
     * @return: java.lang.Float
     **/
    public Float getExchangeLine(WsOrderInfo orderInfo) {
        Float exchangeLine = 0F;
        try {
            String exchangeLineStr = redisCache.getCacheConfig(WS_NOTE_FILTER_EXCHANGE_LINE);
            System.out.println("exchangeLineStr = " + exchangeLineStr);
            exchangeLine = Float.valueOf(exchangeLineStr);
            System.out.println("exchangeLine = " + exchangeLine);
        } catch (Exception e) {
            throw new ServiceException("请让管理员设置正确的保留短信比例，0-1之间!!!");
        }
        if (exchangeLine <= 0 || exchangeLine > 1) {
            throw new ServiceException("请让管理员设置正确的保留短信比例，0-1之间!!!");
        }
        //从国家里面获取是否有该国家的转换率 查询条件为部门和国家id，只会查到一个
        AgencyCountryInfo agencyCountryInfo = new AgencyCountryInfo();
        agencyCountryInfo.setDeptId(orderInfo.getDeptId());
        agencyCountryInfo.setCountryId(orderInfo.getCountryId());
        List<AgencyCountryInfo> agencyCountryInfos = agencyCountryInfoService.selectAgencyCountryInfoList(agencyCountryInfo);
        if (StringUtils.isNotEmpty(agencyCountryInfos)) {
            exchangeLine = agencyCountryInfos.get(0).getProportion();
            System.err.println("国家 exchangeLine = " + exchangeLine);
        }

        //同理，从用户获取转换率 查询条件为用户id和部门只会查到一个
        AgencyUserInfo agencyUserInfo = new AgencyUserInfo();
        agencyUserInfo.setUserId(orderInfo.getUserId());
        agencyUserInfo.setDeptId(orderInfo.getDeptId());
        List<AgencyUserInfo> agencyUserInfos = agencyUserInfoService.selectAgencyUserInfoList(agencyUserInfo);
        if (StringUtils.isNotEmpty(agencyUserInfos)) {
            exchangeLine = agencyUserInfos.get(0).getProportion();
//            System.err.println("用户 exchangeLine = " + exchangeLine);
        }
        return exchangeLine;
    }

    /**
     * @description: 创建文件目录
     * @author: YY
     * @method: createDirectoryIfNotExists
     * @date: 2024/6/8 16:35
     * @param:
     * @param: filePath
     * @return: void
     **/
    public static void createDirectoryIfNotExists(String filePath) {
        Path path = Paths.get(filePath).getParent();
        if (path != null && !Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 修改WS订单信息
     *
     * @param wsOrderInfo WS订单信息
     * @return 结果
     */
    @Override
    public int updateWsOrderInfo(WsOrderInfo wsOrderInfo) {
        //判断是否是重新提交
        WsOrderInfo orderInfo = wsOrderInfoMapper.selectWsOrderInfoById(wsOrderInfo.getId());
        if (!orderInfo.getFileContent().equals(wsOrderInfo.getFileContent())) {
            throw new ServiceException("不可修改文件！！！");
        }

        //判断之前是否更新为已完成或者终止，如果终止或者已完成不可再更新
        if (orderInfo.getStatus().equals(WS_TASK_STATUS_5) || orderInfo.getStatus().equals(WS_TASK_STATUS_4)) {
            throw new ServiceException("订单已完成或者已终止，不可再次更新！！！");
        }

        //判断当前状态为等待中，也就是创建成功，且数据库里面内容是否等于0表示但是没创建成功 这样才需要更新积分
        if (wsOrderInfo.getStatus().equals(WS_TASK_STATUS_1) && orderInfo.getStatus().equals(WS_ORDER_STATUS_0)) {
            //冻结积分
            IntegralHistoryInfo integralHistoryInfo = new IntegralHistoryInfo();
            integralHistoryInfo.setType(WS_INTEGRAL_TYPE_1);
            integralHistoryInfo.setIntegral(wsOrderInfo.getUseIntegral());
            integralHistoryInfo.setUserId(wsOrderInfo.getUserId());
            integralHistoryInfo.setRemark("订单：" + wsOrderInfo.getId() + ",冻结积分：" + integralHistoryInfo.getIntegral());
            integralHistoryInfoService.insertIntegralHistoryInfo(integralHistoryInfo);
        }

        //判断当前状态是否和以前的一样，如果一样就是更新订单信息
        if (wsOrderInfo.getStatus().equals(orderInfo.getStatus())) {
            //判断当前订单是否为已完成，或者已终止如果是这两个状态不可再次更新
            if (wsOrderInfo.getStatus().equals(WS_TASK_STATUS_5) || wsOrderInfo.getStatus().equals(WS_TASK_STATUS_4)) {
                throw new ServiceException("订单已完成或者已终止，不可再次更新！！！");
            }
            //执行更新订单内容并更新到上游平台 //异步去执行
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.submit(() -> executeUpdate(wsOrderInfo));
            System.out.println("executorService = " + executorService);
            return 1;
        }
        System.out.println("继续执行");

        //判断状态是否为已完成 或者为已终止
        if (wsOrderInfo.getStatus().equals(WS_TASK_STATUS_5) || wsOrderInfo.getStatus().equals(WS_TASK_STATUS_4)) {
            //退还积分
            returnIntegral(wsOrderInfo);
        }

        wsOrderInfo.setUpdateTime(DateUtils.getNowDate());
        return wsOrderInfoMapper.updateWsOrderInfo(wsOrderInfo);
    }

    private void executeUpdate(WsOrderInfo wsOrderInfo) {
        //获取基本参数
        String taskUrl = redisCache.getCacheConfig(WS_TASK_UPDATE_API);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);

        //创建一个默认的http客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

            //初始化参数
            taskBuilder.addPart("task_name", new StringBody(wsOrderInfo.getName() != null ? wsOrderInfo.getName() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("text", new StringBody(wsOrderInfo.getCopyContent() != null ? wsOrderInfo.getCopyContent() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("url_list", new StringBody(wsOrderInfo.getLinkContent() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinkContent()) : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("task_type", new StringBody(wsOrderInfo.getSendType(), StandardCharsets.UTF_8));
//            taskBuilder.addPart("send_time", new StringBody(String.valueOf(wsOrderInfo.getSendTime().getTime() / 1000), StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("check_key", new StringBody(wsOrderInfo.getId() != null ? wsOrderInfo.getId() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("task_id", new StringBody(wsOrderInfo.getTaskId() != null ? wsOrderInfo.getTaskId() : "", StandardCharsets.UTF_8));
            //判断是否为超链
            if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_2)) {
                taskBuilder.addPart("kefu_list", new StringBody(wsOrderInfo.getServicePhone() != null ? wsOrderInfo.getServicePhone() : "", StandardCharsets.UTF_8));
            }
            //判断是否为苹果链
            if (wsOrderInfo.getSendType().equals(WS_TASK_TYPE_3)) {
                taskBuilder.addPart("b1", new StringBody(wsOrderInfo.getButtons1() != null ? wsOrderInfo.getButtons1() : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("link1_list", new StringBody(wsOrderInfo.getLinks1() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks1()) : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("b2", new StringBody(wsOrderInfo.getButtons2() != null ? wsOrderInfo.getButtons2() : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("link2_list", new StringBody(wsOrderInfo.getLinks2() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks2()) : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("b3", new StringBody(wsOrderInfo.getButtons3() != null ? wsOrderInfo.getButtons3() : "", StandardCharsets.UTF_8));
                taskBuilder.addPart("link3_list", new StringBody(wsOrderInfo.getLinks3() != null ? StringUtils.parseArrayToString(wsOrderInfo.getLinks3()) : "", StandardCharsets.UTF_8));
            }

            HttpEntity taskEntry = taskBuilder.build();
            taskRequest.setEntity(taskEntry);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);

            HttpEntity taskResponseEntity = taskResponse.getEntity();

            ReturnTaskVo returnTaskVo = new ReturnTaskVo();
            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
                System.err.println("taskResult = " + taskResult);
                returnTaskVo= JSON.parseObject(taskResult,ReturnTaskVo.class);
            }
            //判断返回是否正确
            if (StringUtils.isNotNull(returnTaskVo)&&!returnTaskVo.getStatus().equals(RETURN_STATUS_ERR)) {
                wsOrderInfoMapper.updateWsOrderInfo(wsOrderInfo);
            }
            taskResponse.close();
        } catch (Exception e) { //如果有报错不更新
//            wsOrderInfoMapper.deleteWsOrderInfoById(wsOrderInfo.getId());
            System.out.println("抛出异常");
            System.err.println("e = " + e.getMessage());
            throw new ServiceException("更新订单失败！！！");
        }
    }

    /**
     * @description: 执行订单以及完成操作 退还积分
     * @author: YY
     * @method: returnIntegral
     * @date: 2024/6/19 17:41
     * @param:
     * @param: wsOrderInfo
     * @return: void
     **/
    private void returnIntegral(WsOrderInfo wsOrderInfo) {
        wsOrderInfo.setEndTime(new Date());
        //为已完成 或者已终止
        //获取成功文件 失败的文件

        //获取短信转换率 计算实际积分
        BigDecimal noteExchangeBig = getNoteExchangeBig(wsOrderInfo);
        BigDecimal multiply = noteExchangeBig.multiply(BigDecimal.valueOf(wsOrderInfo.getAccomplishNumber()));
        //保留两位小数
        multiply = multiply.setScale(2, RoundingMode.UP);
        wsOrderInfo.setActualIntegral(multiply);

        //计算实际价格
        //获取税率 并赋值
        String rateStr = redisCache.getCacheConfig(WS_INTEGRAL_EXCHANGE_RATE);
        if (StringUtils.isNull(rateStr)) {
            throw new ServiceException("请联系管理员设置积分税率！！！");
        }
        BigDecimal rate = new BigDecimal(rateStr); // 假设rateStr是一个有效的数字字符串
        RoundingMode roundingMode = RoundingMode.HALF_UP; // 这里使用四舍五入的舍入模式
        BigDecimal price = wsOrderInfo.getActualIntegral().divide(rate, roundingMode);
        wsOrderInfo.setPrices(price);
        wsOrderInfo.setEndTime(DateUtils.getNowDate());

        //更新积分 1、用户积分减少，2、退还用户积分 3、查看差值退还代理人积分
        IntegralHistoryInfo integralHistoryInfo = new IntegralHistoryInfo();
        integralHistoryInfo.setType(WS_INTEGRAL_TYPE_3);
        //用户实际使用积分
        integralHistoryInfo.setIntegral(wsOrderInfo.getActualIntegral());
        integralHistoryInfo.setUserId(wsOrderInfo.getUserId());
        integralHistoryInfo.setRemark("订单：" + wsOrderInfo.getId() + "支出积分：" + wsOrderInfo.getActualIntegral());
        integralHistoryInfoService.insertIntegralHistoryInfo(integralHistoryInfo);

        //退还用户积分
        IntegralHistoryInfo returnIntegralHistory = new IntegralHistoryInfo();
        returnIntegralHistory.setType(WS_INTEGRAL_TYPE_2);
        returnIntegralHistory.setIntegral(wsOrderInfo.getUseIntegral());
        returnIntegralHistory.setUserId(wsOrderInfo.getUserId());
        returnIntegralHistory.setRemark("订单：" + wsOrderInfo.getId() + "退还积分：" + wsOrderInfo.getUseIntegral());
        integralHistoryInfoService.insertIntegralHistoryInfo(returnIntegralHistory);

        //退还代理人积分
        //查询到代理人
        SysDept sysDept = deptService.selectDeptById(wsOrderInfo.getDeptId());
        SysUser agencyUser = userService.selectUserByUserName(sysDept.getLeader() != null ? sysDept.getLeader() : null);
        if (StringUtils.isNotNull(agencyUser)) {
            //如果有代理人则更新
            //计算平台公告的税率
            //获取短信转换率
            String noteExchange = redisCache.getCacheConfig(WS_NOTE_EXCHANGE_RATE);
            if (StringUtils.isNull(noteExchange)) {
                throw new ServiceException("全局短信转换率为空，请联系管理员！！！");
            }
            noteExchangeBig = BigDecimal.valueOf(Float.parseFloat(noteExchange));
            BigDecimal multiplied = noteExchangeBig.multiply(BigDecimal.valueOf(wsOrderInfo.getActualNumber()));
            //计算所得积分
            BigDecimal subtracted = wsOrderInfo.getActualIntegral().subtract(multiplied);
            //判断冻结积分是否小于0
            BigDecimal zero = BigDecimal.valueOf(0);
            //如果小于0则更新
            /*
             * 如果被比较的 `BigDecimal` 在数值上小于此 `BigDecimal`，则返回值小于0。
             * 如果被比较的 `BigDecimal` 在数值上等于此 `BigDecimal`，则返回值等于0。
             * 如果被比较的 `BigDecimal` 在数值上大于此 `BigDecimal`，则返回值大于0
             * */
            if (subtracted.compareTo(zero) > 0) {
                IntegralHistoryInfo returnAgencyIntegralHistory = new IntegralHistoryInfo();
                returnAgencyIntegralHistory.setType(WS_INTEGRAL_TYPE_2);
                returnAgencyIntegralHistory.setIntegral(subtracted);
                returnAgencyIntegralHistory.setUserId(agencyUser.getUserId());
                returnAgencyIntegralHistory.setRemark("订单：" + wsOrderInfo.getId() + "退还积分：" + subtracted);
                integralHistoryInfoService.insertIntegralHistoryInfo(returnAgencyIntegralHistory);
            }
        }
    }

    @DataScope(userAlias = "tb_ws_order_info", deptAlias = "tb_ws_order_info")
    @Override
    public int getTaskStatusWithUpdateWsOrderStatus(WsOrderInfo wsOrderInfo) {
        //查询未完成的订单  //更新他们的订单
        List<WsOrderInfo> wsOrderInfos = wsOrderInfoMapper.selectWsOrderInfoList(wsOrderInfo);

        if (StringUtils.isEmpty(wsOrderInfos)) {
            throw new ServiceException("没有需要更新的订单！！！");
        }

        //获取基本信息 url apiToken userId
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);
        String taskInfoUrl = redisCache.getCacheConfig(WS_TASK_INFO_URL);

        //打开线程
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (WsOrderInfo orderInfo : wsOrderInfos) {
            //如果当前状态为已完成或者已终止则不再更新
            if (orderInfo.getStatus().equals(WS_TASK_STATUS_4) || orderInfo.getStatus().equals(WS_TASK_STATUS_5)) {
                continue;
            }
            executorService.submit(() -> {
                taskStatusWithUpdateWsOrderStatus(orderInfo, taskInfoUrl, userId, apiToken);
            });
        }
        return 1;
    }

    /**
     * @description: 查询订单状态并更新，如果状态相同则不更新
     * @author: YY
     * @method: taskStatusWithUpdateWsOrderStatus
     * @date: 2024/6/14 18:42
     * @param:
     * @param: wsOrderInfo
     * @param: url
     * @param: userId
     * @param: apiToken
     * @return: void
     **/
    public void taskStatusWithUpdateWsOrderStatus(WsOrderInfo wsOrderInfo, String url, String userId, String apiToken) {
        //判断是否有taskId，没有证明创建未成功
        if (StringUtils.isNull(wsOrderInfo.getTaskId())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        try {
            String taskId = wsOrderInfo.getTaskId();
            System.err.println("taskId = " + taskId);
            String s = sendJsonByGetReq(url + "/" + taskId, reqParams, "UTF-8");
            ReturnTaskVo returnTaskVo = JSON.parseObject(s, ReturnTaskVo.class);
            System.out.println("returnTaskVo = " + returnTaskVo);
            //判断状态是否相同 相同直接返回
            String status = returnTaskVo.getTask_info().getStatus();
            if (status.equals(wsOrderInfo.getStatus())) {
                return;
            }
            wsOrderInfo.setStatus(returnTaskVo.getTask_info().getStatus());
            wsOrderInfo.setSendTime(returnTaskVo.getTask_info().getSend_time() != null ? new Date(Long.parseLong(returnTaskVo.getTask_info().getSend_time()) * 1000) : null);
            updateWsOrderInfo(wsOrderInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量删除WS订单信息
     *
     * @param ids 需要删除的WS订单信息主键
     * @return 结果
     */
    @Override
    public int deleteWsOrderInfoByIds(String[] ids) {
        return wsOrderInfoMapper.deleteWsOrderInfoByIds(ids);
    }

    /**
     * 删除WS订单信息信息
     *
     * @param id WS订单信息主键
     * @return 结果
     */
    @Override
    public int deleteWsOrderInfoById(String id) {
        return wsOrderInfoMapper.deleteWsOrderInfoById(id);
    }

    @Override
    public WsOrderInfo computeIntegral(WsOrderInfo orderInfo) {
        String filePath = RuoYiConfig.getProfile() + FileUtils.initFilePath(orderInfo.getFileContent());
        if (StringUtils.isNull(orderInfo.getCountryId())) {
            throw new RuntimeException("请先选择国家！！！");
        }

        BigDecimal noteExchangeBig = getNoteExchangeBig(orderInfo);

        long lineCount = getFileLines(filePath);
        orderInfo.setOrderNumber(lineCount);
        BigDecimal multiply = noteExchangeBig.multiply(BigDecimal.valueOf(lineCount));
        //保留两位小数
        multiply = multiply.setScale(2, RoundingMode.UP);
        orderInfo.setUseIntegral(multiply);
        Long maxLine = 0L;
        try {
            String maxLineStr = redisCache.getCacheConfig(WS_NOTE_MAX_LINE);
            System.out.println("maxLineStr = " + maxLineStr);
            maxLine = Long.valueOf(maxLineStr);

        } catch (Exception e) {
            throw new ServiceException("请让管理员设置正确的最大行数，正整数");
        }
        if (maxLine < lineCount) {
            throw new ServiceException("数据不可超过：" + maxLine + "行!!!");
        }
        return orderInfo;
    }

    @Override
    public int getIsCreateTask() {
        //获取基本参数
        String apiUrl = redisCache.getCacheConfig(WS_TASK_CREATE_TASK_API);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        try {
            String s = sendJsonByGetReq(apiUrl, reqParams, "UTF-8");
            ReturnTaskVo returnTaskVo = JSON.parseObject(s, ReturnTaskVo.class);
            return returnTaskVo.getNew_task_status();
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new ServiceException("获取失败！！！");
        }
    }

    @Override
    public void wsTaskUpdateTask() {
        //先查询到状态不为已完成、终止的任务
        List<WsOrderInfo> wsOrderInfoList = wsOrderInfoMapper.selectWithStatusNo4ANd5WsOrderInfoList();
        //创建线程来执行
        if (StringUtils.isEmpty(wsOrderInfoList)) {
            return;
        }

        //获取基本信息 url apiToken userId
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);
        String taskInfoUrl = redisCache.getCacheConfig(WS_TASK_INFO_URL);

        //打开线程
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (WsOrderInfo orderInfo : wsOrderInfoList) {
            executorService.submit(() -> {
                wsTaskGetTaskInfo(orderInfo, taskInfoUrl, userId, apiToken);
            });
        }
    }

    /**
     * @description: 定时任务更新订单
     * @author: YY
     * @method: wsTaskGetTaskInfo
     * @date: 2024/6/20 14:21
     * @param:
     * @param: orderInfo
     * @param: taskInfoUrl
     * @param: userId
     * @param: apiToken
     * @return: void
     **/
    private void wsTaskGetTaskInfo(WsOrderInfo wsOrderInfo, String url, String userId, String apiToken) {
        //判断是否有taskId，没有证明创建未成功
        String taskId = wsOrderInfo.getTaskId();
        if (StringUtils.isNull(taskId)) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        try {
            System.out.println("taskId = " + taskId);
            String s = sendJsonByGetReq(url + "/" + taskId, reqParams, "UTF-8");
            ReturnTaskVo returnTaskVo = JSON.parseObject(s, ReturnTaskVo.class);
            System.out.println("returnTaskVo = " + returnTaskVo);
            //判断状态是否相同 相同直接返回
            Integer numSucc = returnTaskVo.getTask_info().getNum_succ();
            System.out.println("numSucc = " + numSucc);
            //如果有完成数量，更新完成数量
            if (StringUtils.isNotNull(numSucc) && numSucc != 0) {
                //判断完成比例为提交比例的多少
                //先把两个数量转换为float
                Float numSuccF = Float.parseFloat(numSucc.toString());
                Float actualNumberF = Float.parseFloat(wsOrderInfo.getActualNumber().toString());
                Float ratio = actualNumberF / numSuccF;
                wsOrderInfo.setActualNumber((long) (ratio * wsOrderInfo.getOptimizedNumber()));
            }
            wsOrderInfo.setStatus(returnTaskVo.getTask_info().getStatus());
            wsOrderInfo.setSendTime(returnTaskVo.getTask_info().getSend_time() != null ? new Date(Long.parseLong(returnTaskVo.getTask_info().getSend_time()) * 1000) : null);
            wsTaskUpdateTaskInfo(wsOrderInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void wsTaskUpdateTaskInfo(WsOrderInfo wsOrderInfo) {
        //判断是否是重新提交
        WsOrderInfo orderInfo = wsOrderInfoMapper.selectWsOrderInfoById(wsOrderInfo.getId());
        if (!orderInfo.getFileContent().equals(orderInfo.getFileContent())) {
            throw new ServiceException("不可修改文件！！！");
        }

        //判断当前状态为等待中，也就是创建成功，且数据库里面内容是否等于0表示但是没创建成功 这样才需要更新积分
        if (wsOrderInfo.getStatus().equals(WS_TASK_STATUS_1) && orderInfo.getStatus().equals(WS_ORDER_STATUS_0)) {
            //冻结积分
            IntegralHistoryInfo integralHistoryInfo = new IntegralHistoryInfo();
            integralHistoryInfo.setType(WS_INTEGRAL_TYPE_1);
            integralHistoryInfo.setIntegral(wsOrderInfo.getUseIntegral());
            integralHistoryInfo.setUserId(wsOrderInfo.getUserId());
            integralHistoryInfo.setRemark("订单：" + wsOrderInfo.getId() + ",冻结积分：" + integralHistoryInfo.getIntegral());
            integralHistoryInfoService.insertIntegralHistoryInfo(integralHistoryInfo);
        }

        //判断状态是否为已完成 或者为已终止
        if (wsOrderInfo.getStatus().equals(WS_TASK_STATUS_5) || wsOrderInfo.getStatus().equals(WS_TASK_STATUS_4)) {
            //计算当前完成的数量
            getSuccess(wsOrderInfo);
            //退还积分
            returnIntegral(wsOrderInfo);
        }

        wsOrderInfo.setUpdateTime(DateUtils.getNowDate());
        wsOrderInfoMapper.updateWsOrderInfo(wsOrderInfo);
    }

    /**
     * @description: 获取当前完成数量以及积分
     * @author: YY
     * @method: getSuccess
     * @date: 2024/6/20 15:26
     * @param:
     * @param: wsOrderInfo
     * @return: void
     **/
    private void getSuccess(WsOrderInfo wsOrderInfo) {
        //获取文件路径
        String initFilePath = FileUtils.initFilePath(wsOrderInfo.getFileContent());
        String inputFilePath = RuoYiConfig.getProfile() + initFilePath;
        String fileName = FileUtils.getName(inputFilePath);
        String outputPath = FileUtils.getFilePath(inputFilePath);
        String discardedFilePath = outputPath + File.separator + "discarded" + File.separator + fileName;
        String name = FileUtils.getNameNotSuffix(fileName);
        String inputSuccessFilePath = outputPath + File.separator + "success" + File.separator + name + ".xlsx";
        //获取存储到数据库的位置
        String lastFilePath = FileUtils.getFilePath(wsOrderInfo.getFileContent());
        String successFilePath = lastFilePath + File.separator + "success" + File.separator + name + ".xlsx";

        //获取成功和失败数量
        Object fileUrl = redisCache.getCacheConfig(WS_TASK_GET_FILE_API);
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
        Long fileNumbers = ExcelFileUtil.writeExcelFile(succString, discardedFilePath, wsOrderInfo.getOrderNumber(), failString, inputSuccessFilePath);
        wsOrderInfo.setAccomplishNumber(fileNumbers);
        wsOrderInfo.setResSuccFile(successFilePath);
    }

    // 检查字符串是否包含非数字字符的函数
    private static boolean containsNonNumeric(String s) {
        Pattern p = Pattern.compile("[^0-9\\n]"); // 正则表达式匹配非数字和非换行符的字符
        Matcher m = p.matcher(s);
        return m.find();
    }

    @Override
    public int updateSendTime(WsOrderInfo wsOrderInfo) {
        judgeStatusIs4Or5(wsOrderInfo);
        //判断当前状态是否为待发送
        System.err.println("wsOrderInfo = " + wsOrderInfo);
        if (!wsOrderInfo.getStatus().equals(WS_TASK_STATUS_1)) {
            //如果不是则直接返回
            throw new ServiceException("当前订单不是等待开始，不可修改发送时间！！！");
        }
        //创建异步发送订单
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(()->executeUpdateSendTime(wsOrderInfo));
        return 1;
    }

    /**
     * 判断订单是否已完成或者终止
     * @param wsOrderInfo
     */
    private void judgeStatusIs4Or5(WsOrderInfo wsOrderInfo) {
        //判断以前状态 是否为已终止或者已完成
        WsOrderInfo oldOrderInfo = wsOrderInfoMapper.selectWsOrderInfoById(wsOrderInfo.getId());
        if (oldOrderInfo.getStatus().equals(WS_TASK_STATUS_5)||oldOrderInfo.getStatus().equals(WS_TASK_STATUS_4)) {
            throw new ServiceException("当前订单已经完成！！！");
        }
    }

    /**
     * @description: 更新发送时间
     * @author: YY
     * @method: executeUpdateSendTime
     * @date: 2024/6/19 18:03
     * @param:
     * @param: wsOrderInfo
     * @return: void
     **/
    private void executeUpdateSendTime(WsOrderInfo wsOrderInfo) {
        //获取基本参数
        String taskUrl = redisCache.getCacheConfig(WS_TASK_UPDATE_SENDTIME_API);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);

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

            if (StringUtils.isNull(taskEntry)) {
                return;
            }
            ReturnTaskVo returnTaskVo = new ReturnTaskVo();
            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
//                System.err.println("taskResult = " + taskResult);
                returnTaskVo= JSON.parseObject(taskResult,ReturnTaskVo.class);
            }
            //判断返回是否正确
            if (StringUtils.isNotNull(returnTaskVo)&&!returnTaskVo.getStatus().equals(RETURN_STATUS_ERR)) {
                wsOrderInfo.setUpdateTime(DateUtils.getNowDate());
                wsOrderInfoMapper.updateWsOrderInfo(wsOrderInfo);
            }
        } catch (Exception e) {
//            wsOrderInfoMapper.deleteWsOrderInfoById(wsOrderInfo.getId());
            System.err.println("e = " + e.getMessage());
            throw new ServiceException("更新订单发送时间失败！！！");
        }
    }

    @Override
    public int updateSendStatus(WsOrderInfo wsOrderInfo) {
        //判断以前状态 是否为已终止或者已完成
        judgeStatusIs4Or5(wsOrderInfo);
        //判断传来的状态是否不是234
        if (!wsOrderInfo.getStatus().equals(WS_ORDER_STATUS_4)&&!wsOrderInfo.getStatus().equals(WS_ORDER_STATUS_3)&&!wsOrderInfo.getStatus().equals(WS_ORDER_STATUS_2)) {
            throw new ServiceException("当前订单状态不正确，状态只能修改为发送中、暂停、终止！！！");
        }
        //创建异步更新
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(()->executeUpdateSendStatus(wsOrderInfo));
        return 1;
    }

    /**
     * @description: 更新发送状态
     * @author: YY
     * @method: executeUpdateSendTime
     * @date: 2024/6/19 18:03
     * @param:
     * @param: wsOrderInfo
     * @return: void
     **/
    private void executeUpdateSendStatus(WsOrderInfo wsOrderInfo) {
        //获取基本参数
        String taskUrl = redisCache.getCacheConfig(WS_TASK_UPDATE_STATUS_API);
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);

        //创建一个默认的http客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpPost taskRequest = new HttpPost(taskUrl);
            MultipartEntityBuilder taskBuilder = MultipartEntityBuilder.create();

            taskBuilder.addPart("new_status", new StringBody(wsOrderInfo.getStatus() != null ? wsOrderInfo.getStatus() : "", StandardCharsets.UTF_8));
            taskBuilder.addPart("api_token", new StringBody(apiToken, StandardCharsets.UTF_8));
            taskBuilder.addPart("user_id", new StringBody(userId, StandardCharsets.UTF_8));
            taskBuilder.addPart("task_id", new StringBody(wsOrderInfo.getTaskId() != null ? wsOrderInfo.getTaskId() : "", StandardCharsets.UTF_8));

            HttpEntity taskEntry = taskBuilder.build();
            taskRequest.setEntity(taskEntry);

            CloseableHttpResponse taskResponse = httpClient.execute(taskRequest);

            HttpEntity taskResponseEntity = taskResponse.getEntity();

            if (StringUtils.isNull(taskEntry)) {
                return;
            }
            ReturnTaskVo returnTaskVo = new ReturnTaskVo();
            if (StringUtils.isNotNull(taskEntry)) {
                String taskResult = EntityUtils.toString(taskResponseEntity, StandardCharsets.UTF_8);
//                System.err.println("taskResult = " + taskResult);
                returnTaskVo= JSON.parseObject(taskResult,ReturnTaskVo.class);
            }
            //判断返回是否正确
            if (StringUtils.isNotNull(returnTaskVo)&&!returnTaskVo.getStatus().equals(RETURN_STATUS_ERR)) {
                //判断状态是否为已终止
                if (wsOrderInfo.getStatus().equals(WS_TASK_STATUS_4)) {
                    //计算当前完成的数量
                    getSuccess(wsOrderInfo);
                    //退还积分
                    returnIntegral(wsOrderInfo);
                }
                wsOrderInfo.setUpdateTime(DateUtils.getNowDate());
                wsOrderInfoMapper.updateWsOrderInfo(wsOrderInfo);
            }
        } catch (Exception e) {
//            wsOrderInfoMapper.deleteWsOrderInfoById(wsOrderInfo.getId());
            System.err.println("e = " + e.getMessage());
            throw new ServiceException("更新订单发送时间失败！！！");
        }
    }
}

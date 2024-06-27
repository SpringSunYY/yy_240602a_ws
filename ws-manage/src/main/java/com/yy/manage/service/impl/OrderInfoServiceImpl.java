package com.yy.manage.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
import com.yy.manage.mapper.AgencyCountryInfoMapper;
import com.yy.manage.mapper.AgencyUserInfoMapper;
import com.yy.manage.mapper.IntegralHistoryInfoMapper;
import com.yy.manage.service.*;
import com.yy.system.service.ISysDeptService;
import com.yy.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.OrderInfoMapper;
import org.springframework.transaction.annotation.Transactional;

import static com.yy.common.constant.ConfigConstants.*;
import static com.yy.common.constant.DictDataConstants.*;

/**
 * 订单信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-07
 */
@Service
public class OrderInfoServiceImpl implements IOrderInfoService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;

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
     * 查询订单信息
     *
     * @param id 订单信息主键
     * @return 订单信息
     */
    @Override
    public OrderInfo selectOrderInfoById(String id) {
        return orderInfoMapper.selectOrderInfoById(id);
    }

    /**
     * 查询订单信息列表
     *
     * @param orderInfo 订单信息
     * @return 订单信息
     */
    @DataScope(deptAlias = "tb_order_info", userAlias = "tb_order_info")
    @Override
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo) {
        if (!SecurityUtils.hasPermi("look:is:delete")) {
            orderInfo.setIsDelete(WS_IS_DELETE_0);
        }
        List<OrderInfo> orderInfos = orderInfoMapper.selectOrderInfoList(orderInfo);
        for (OrderInfo info : orderInfos) {
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
        return orderInfos;
    }

    /**
     * 新增订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertOrderInfo(OrderInfo orderInfo) {
        String id = IdUtils.fastSimpleUUID();
        OperationHistoryInfo operationHistoryInfo = new OperationHistoryInfo();
        operationHistoryInfo.setOrderId(id);
        operationHistoryInfo.setRemark("创建订单");
        historyInfoService.insertOperationHistoryInfo(operationHistoryInfo);

        //1、获取税率 并赋值
        String rateStr = redisCache.getCacheConfig(WS_INTEGRAL_EXCHANGE_RATE);
        if (StringUtils.isNull(rateStr)) {
            throw new ServiceException("请联系管理员设置积分税率！！！");
        }
        BigDecimal rate = new BigDecimal(rateStr); // 假设rateStr是一个有效的数字字符串
        RoundingMode roundingMode = RoundingMode.HALF_UP; // 这里使用四舍五入的舍入模式
        BigDecimal price = orderInfo.getUseIntegral().divide(rate, roundingMode);
        orderInfo.setPrices(price);
//        System.out.println("price = " + price);

        orderInfo.setDeptId(SecurityUtils.getDeptId());
        orderInfo.setUserId(SecurityUtils.getUserId());
        orderInfo.setStatus(WS_ORDER_STATUS_0);
        orderInfo.setIsDelete(WS_IS_DELETE_0);
        orderInfo.setId(id);
        orderInfo.setCreateTime(DateUtils.getNowDate());

        //查询用户可用积分是否比现在少
        IntegralInfo integralInfo = new IntegralInfo();
        integralInfo.setUserId(orderInfo.getUserId());
        List<IntegralInfo> integralInfos = integralInfoService.selectIntegralInfoList(integralInfo);
        if (StringUtils.isEmpty(integralInfos) || integralInfos.get(0).getIntegral().compareTo(orderInfo.getUseIntegral()) < 0) {
            throw new ServiceException("您的积分不足！！！");
        }

        //冻结积分
        IntegralHistoryInfo integralHistoryInfo = new IntegralHistoryInfo();
        integralHistoryInfo.setType(WS_INTEGRAL_TYPE_1);
        integralHistoryInfo.setIntegral(orderInfo.getUseIntegral());
        integralHistoryInfo.setUserId(SecurityUtils.getUserId());
        integralHistoryInfo.setRemark("订单：" + id + ",冻结积分：" + integralHistoryInfo.getIntegral());
        integralHistoryInfoService.insertIntegralHistoryInfo(integralHistoryInfo);
        return orderInfoMapper.insertOrderInfo(orderInfo);
    }

    /**
     * 修改订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    @Transactional
    @Override
    public int updateOrderInfo(OrderInfo orderInfo) {
        //查询之前审核 不可低于之前审核
        OrderInfo oldOrder = orderInfoMapper.selectOrderInfoById(orderInfo.getId());
        if (Integer.parseInt(oldOrder.getStatus()) > Integer.parseInt(orderInfo.getStatus())) {
            throw new ServiceException("不可逆向审核");
        }
        if (oldOrder.getStatus().equals(WS_ORDER_STATUS_4)) {
            throw new ServiceException("订单已经完成不可修改！！！");
        }
        if (orderInfo.getStatus().equals(WS_ORDER_STATUS_0)) {
            //如果是尚未提交审核订单需要重新优化
            computeIntegral(orderInfo);
            //国家文件不可更改
            if (!oldOrder.getFileCotent().equals(orderInfo.getFileCotent())||!orderInfo.getCountryId().equals(orderInfo.getCountryId())) {
                throw new ServiceException("国家和文件不可修改！！！");
            }
        }

        orderInfo.setUpdateTime(DateUtils.getNowDate());
        OperationHistoryInfo operationHistoryInfo = new OperationHistoryInfo();
        operationHistoryInfo.setDeptId(oldOrder.getDeptId());
        operationHistoryInfo.setOrderId(orderInfo.getId());
        operationHistoryInfo.setUserId(orderInfo.getUserId());
        if (orderInfo.getStatus().equals(WS_ORDER_STATUS_1)) {
            operationHistoryInfo.setRemark("提交订单，" + "执行：" + SecurityUtils.getUsername());
        }
        if (orderInfo.getStatus().equals(WS_ORDER_STATUS_2)) {
            if (oldOrder.getStatus().equals(WS_ORDER_STATUS_0)) {
                return 0;
            }
            try {
                operationHistoryInfo.setRemark("优化订单，" + "执行人：" + SecurityUtils.getUsername());
            } catch (Exception e) {
                operationHistoryInfo.setCreateBy(orderInfo.getOptimizeUser());
                operationHistoryInfo.setRemark("优化订单，" + "执行人：" + orderInfo.getOptimizeUser());
            }
        }
        if (orderInfo.getStatus().equals(WS_ORDER_STATUS_3)) {
            operationHistoryInfo.setRemark("订单已开始，" + "执行人：" + SecurityUtils.getUsername());
            orderInfo.setSendTime(DateUtils.getNowDate());
        }
        if (orderInfo.getStatus().equals(WS_ORDER_STATUS_4)) {

            //获取短信转换率 计算实际积分
            BigDecimal noteExchangeBig = getNoteExchangeBig(orderInfo);
            BigDecimal multiply = noteExchangeBig.multiply(BigDecimal.valueOf(orderInfo.getAccomplishNumber()));
            //保留两位小数
            multiply = multiply.setScale(2, RoundingMode.UP);
            orderInfo.setActualIntegral(multiply);

            //计算实际价格
            //获取税率 并赋值
            String rateStr = redisCache.getCacheConfig(WS_INTEGRAL_EXCHANGE_RATE);
            if (StringUtils.isNull(rateStr)) {
                throw new ServiceException("请联系管理员设置积分税率！！！");
            }
            BigDecimal rate = new BigDecimal(rateStr); // 假设rateStr是一个有效的数字字符串
            RoundingMode roundingMode = RoundingMode.HALF_UP; // 这里使用四舍五入的舍入模式
            BigDecimal price = orderInfo.getActualIntegral().divide(rate, roundingMode);
            orderInfo.setPrices(price);

            operationHistoryInfo.setRemark("订单已完成，" + "执行人：" + SecurityUtils.getUsername());
            orderInfo.setEndTime(DateUtils.getNowDate());

            //更新积分 1、用户积分减少，2、退还用户积分 3、查看差值退还代理人积分
            IntegralHistoryInfo integralHistoryInfo = new IntegralHistoryInfo();
            integralHistoryInfo.setType(WS_INTEGRAL_TYPE_3);
            //用户实际使用积分
            integralHistoryInfo.setIntegral(orderInfo.getActualIntegral());
            integralHistoryInfo.setUserId(orderInfo.getUserId());
            integralHistoryInfo.setRemark("订单：" + orderInfo.getId() + "支出积分：" + orderInfo.getActualIntegral());
            integralHistoryInfoService.insertIntegralHistoryInfo(integralHistoryInfo);

            //退还用户积分
            IntegralHistoryInfo returnIntegralHistory = new IntegralHistoryInfo();
            returnIntegralHistory.setType(WS_INTEGRAL_TYPE_2);
            returnIntegralHistory.setIntegral(orderInfo.getUseIntegral());
            returnIntegralHistory.setUserId(orderInfo.getUserId());
            returnIntegralHistory.setRemark("订单：" + orderInfo.getId() + "退还积分：" + orderInfo.getUseIntegral());
            integralHistoryInfoService.insertIntegralHistoryInfo(returnIntegralHistory);

            //更新冻结的记录


            //退还代理人积分
            //查询到代理人
            SysDept sysDept = deptService.selectDeptById(orderInfo.getDeptId());
            SysUser agencyUser = userService.selectUserByUserName(sysDept.getLeader() != null ? sysDept.getLeader() : null);
            //System.out.println("agencyUser = " + agencyUser);
            if (StringUtils.isNotNull(agencyUser)) {
                //如果有代理人则更新
                //计算平台公告的税率
                //获取短信转换率
                String noteExchange = redisCache.getCacheConfig(WS_NOTE_EXCHANGE_RATE);
                if (StringUtils.isNull(noteExchange)) {
                    throw new ServiceException("全局短信转换率为空，请联系管理员！！！");
                }
                noteExchangeBig = BigDecimal.valueOf(Float.parseFloat(noteExchange));
                BigDecimal multiplied = noteExchangeBig.multiply(BigDecimal.valueOf(orderInfo.getActualNumber()));
                //计算所得积分
                BigDecimal subtracted = orderInfo.getActualIntegral().subtract(multiplied);
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
                    returnAgencyIntegralHistory.setRemark("订单：" + orderInfo.getId() + "退还积分：" + subtracted);
                    integralHistoryInfoService.insertIntegralHistoryInfo(returnAgencyIntegralHistory);
                }
            }
        }
        if (StringUtils.isNull(operationHistoryInfo.getRemark())) {
            operationHistoryInfo.setRemark("修改订单内容");
        }
        historyInfoService.insertOperationHistoryInfo(operationHistoryInfo);
        return orderInfoMapper.updateOrderInfo(orderInfo);
    }


    /**
     * 批量删除订单信息
     *
     * @param ids 需要删除的订单信息主键
     * @return 结果
     */
    @Override
    public int deleteOrderInfoByIds(String[] ids) {
        return orderInfoMapper.deleteOrderInfoByIds(ids);
    }

    /**
     * 删除订单信息信息
     *
     * @param id 订单信息主键
     * @return 结果
     */
    @Override
    public int deleteOrderInfoById(String id) {
        return orderInfoMapper.deleteOrderInfoById(id);
    }

    @Override
    public OrderInfo computeIntegral(OrderInfo orderInfo) {
        String filePath = RuoYiConfig.getProfile() + FileUtils.initFilePath(orderInfo.getFileCotent());
        if (StringUtils.isNull(orderInfo.getCountryId())) {
            throw new RuntimeException("请先选择国家！！！");
        }
        orderInfo.setUserId(SecurityUtils.getUserId());
        orderInfo.setDeptId(SecurityUtils.getDeptId());
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
            //System.out.println("maxLineStr = " + maxLineStr);
            maxLine = Long.valueOf(maxLineStr);

        } catch (Exception e) {
            throw new ServiceException("请让管理员设置正确的最大行数，正整数");
        }
        if (maxLine < lineCount) {
            throw new ServiceException("数据不可超过：" + maxLine + "行!!!");
        }
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
    private BigDecimal getNoteExchangeBig(OrderInfo orderInfo) {
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

    @Override
    public int optimize() {
        OrderInfo info = new OrderInfo();
        info.setDeptId(SecurityUtils.getDeptId());
        info.setStatus(WS_ORDER_STATUS_1);
        List<OrderInfo> orderInfos = orderInfoMapper.selectOrderInfoList(info);
        if (StringUtils.isEmpty(orderInfos)) {
            throw new ServiceException("无需优化订单，所有订单均以优化！！！");
        }
        //获取优化人基本信息
        String username = SecurityUtils.getUsername();
        ExecutorService executorService = Executors.newFixedThreadPool(orderInfos.size());
        List<Future<Integer>> futures = new ArrayList<>();
        for (OrderInfo orderInfo : orderInfos) {
            Future<Integer> future = executorService.submit(() -> {
                orderInfo.setOptimizeUser(username);
//                orderInfo.setOptimizeUserId(userId);
                return optimizeOrder(orderInfo);

            });
            futures.add(future);
        }

        // 等待所有操作完成
        for (Future<Integer> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        executorService.shutdown();
        return 1;
    }

    private int optimizeOrder(OrderInfo orderInfo) {
        if (StringUtils.isNull(orderInfo.getFileCotent()) || orderInfo.getStatus().equals(WS_ORDER_STATUS_0)) {
            return 0;
        }

        String[] fileStrings = filterPhoneNumbers(orderInfo);

        String lastFilePath = FileUtils.getFilePath(orderInfo.getFileCotent());
        orderInfo.setFileFilter(lastFilePath + File.separator + "filter" + File.separator + fileStrings[1]);

        //从过滤文件获取行数
        Long fileLines = getFileLines(fileStrings[0]);
        orderInfo.setActualNumber(fileLines);
        orderInfo.setOptimizedNumber(orderInfo.getOrderNumber() - fileLines);

        //获取短信转换率 计算实际积分
        BigDecimal noteExchangeBig = getNoteExchangeBig(orderInfo);
        BigDecimal multiply = noteExchangeBig.multiply(BigDecimal.valueOf(fileLines));
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
        return updateOrderInfo(orderInfo);
    }

    public Long getFileLines(String filePath) {
        // 使用BufferedReader读取文件行数
        Path path = Paths.get(filePath);
        Long lineCount = 0L;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            lineCount = reader.lines().count();
//            System.out.println("Number of lines = " + lineCount);
        } catch (IOException e) {
//            System.out.println("e.getMessage() = " + e.getMessage());
            throw new ServiceException("文件检查失败，或许是文件太大！！！");
        } catch (NullPointerException e) {
            throw new ServiceException("文件检查失败！！！");
        } catch (Exception e) {
//            System.out.println("e = " + e.getMessage());
            throw new ServiceException("运行异常，请联系管理员查看是否全局短信转换率格式是否正确！！！");
        }
        return lineCount;
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
    public String[] filterPhoneNumbers(OrderInfo orderInfo) {
        try {
            //获取文件路径
            String initFilePath = FileUtils.initFilePath(orderInfo.getFileCotent());
            String inputFilePath = RuoYiConfig.getProfile() + initFilePath;
            String fileName = FileUtils.getName(inputFilePath);
            String outputPath = FileUtils.getFilePath(inputFilePath);
            String outputFilePath = outputPath + File.separator + "filter" + File.separator + fileName;
            String discardedPath = outputPath + File.separator + "discarded" + File.separator + fileName;
            CountryInfo countryInfo = countryInfoService.selectCountryInfoById(orderInfo.getCountryId());
            String prefix = countryInfo.getPhoneCode();
            // 创建必要的目录
            createDirectoryIfNotExists(outputFilePath);
            createDirectoryIfNotExists(discardedPath);

            // 读取输入文件的所有行
            List<String> phoneNumbers = Files.readAllLines(Paths.get(inputFilePath));

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

            // 划分保留和丢弃的号码
            List<String> retainedNumbers = filteredList.subList(0, retainSize);
            List<String> discardedNumbers = filteredList.subList(retainSize,totalSize);

            // 将保留的号码写入输出文件
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
                for (String number : retainedNumbers) {
                    writer.write(number);
                    writer.newLine();
                }
            }
            return new String[]{outputFilePath, fileName};
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("文件过滤失败！！！");
        }
    }

    public Float getExchangeLine(OrderInfo orderInfo) {
        Float exchangeLine = 0F;
        try {
            String exchangeLineStr = redisCache.getCacheConfig(WS_NOTE_FILTER_EXCHANGE_LINE);
            //System.out.println("exchangeLineStr = " + exchangeLineStr);
            exchangeLine = Float.valueOf(exchangeLineStr);
            //System.out.println("exchangeLine = " + exchangeLine);
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
        List<AgencyCountryInfo> agencyCountryInfos = agencyCountryInfoMapper.selectAgencyCountryInfoList(agencyCountryInfo);
        if (StringUtils.isNotEmpty(agencyCountryInfos)) {
            exchangeLine = agencyCountryInfos.get(0).getProportion();
//            System.err.println("国家 exchangeLine = " + exchangeLine);
        }

        //同理，从用户获取转换率 查询条件为用户id和部门只会查到一个
        AgencyUserInfo agencyUserInfo = new AgencyUserInfo();
        agencyUserInfo.setUserId(orderInfo.getUserId());
        agencyUserInfo.setDeptId(orderInfo.getDeptId());
        List<AgencyUserInfo> agencyUserInfos = agencyUserInfoMapper.selectAgencyUserInfoList(agencyUserInfo);
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
}

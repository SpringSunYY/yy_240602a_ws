package com.yy.manage.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.yy.common.annotation.DataScope;
import com.yy.common.core.domain.entity.SysDept;
import com.yy.common.core.domain.entity.SysUser;
import com.yy.common.core.redis.RedisCache;
import com.yy.common.exception.ServiceException;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.IntegralInfo;
import com.yy.manage.service.IIntegralInfoService;
import com.yy.system.service.ISysDeptService;
import com.yy.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.IntegralHistoryInfoMapper;
import com.yy.manage.domain.IntegralHistoryInfo;
import com.yy.manage.service.IIntegralHistoryInfoService;
import org.springframework.transaction.annotation.Transactional;

import static com.yy.common.constant.ConfigConstants.WS_INTEGRAL_EXCHANGE_RATE;
import static com.yy.common.constant.DictDataConstants.*;

/**
 * 积分记录信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-06
 */
@Service
public class IntegralHistoryInfoServiceImpl implements IIntegralHistoryInfoService {
    @Autowired
    private IntegralHistoryInfoMapper integralHistoryInfoMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IIntegralInfoService integralInfoService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询积分记录信息
     *
     * @param id 积分记录信息主键
     * @return 积分记录信息
     */
    @Override
    public IntegralHistoryInfo selectIntegralHistoryInfoById(String id) {
        return integralHistoryInfoMapper.selectIntegralHistoryInfoById(id);
    }

    /**
     * 查询积分记录信息列表
     *
     * @param integralHistoryInfo 积分记录信息
     * @return 积分记录信息
     */
    @DataScope(userAlias = "tb_integral_history_info", deptAlias = "tb_integral_history_info")
    @Override
    public List<IntegralHistoryInfo> selectIntegralHistoryInfoList(IntegralHistoryInfo integralHistoryInfo) {
        if (!SecurityUtils.hasPermi("look:is:delete")) {
            integralHistoryInfo.setIsDelete(WS_IS_DELETE_0);
        }
        List<IntegralHistoryInfo> integralHistoryInfos = integralHistoryInfoMapper.selectIntegralHistoryInfoList(integralHistoryInfo);
        for (IntegralHistoryInfo info : integralHistoryInfos) {
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

        }
        return integralHistoryInfos;
    }

    /**
     * 新增积分记录信息
     *
     * @param integralHistoryInfo 积分记录信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertIntegralHistoryInfo(IntegralHistoryInfo integralHistoryInfo) {
        //1、获取税率 并赋值
        String rateStr = redisCache.getCacheConfig(WS_INTEGRAL_EXCHANGE_RATE);
        if (StringUtils.isNull(rateStr)) {
            throw new ServiceException("请联系管理员设置税率！！！");
        }
        BigDecimal rate;
        try {
            rate = new BigDecimal(rateStr); // 假设rateStr是一个有效的数字字符串
        } catch (Exception e) {
            throw new ServiceException("请联系管理员设置正确的税率！！！");
        }

        if (StringUtils.isNotNull(integralHistoryInfo.getMoney())) {
            BigDecimal money = integralHistoryInfo.getMoney();
            integralHistoryInfo.setIntegral(money.multiply(rate));
        } else {
            BigDecimal divide = integralHistoryInfo.getIntegral().divide(rate, 2, RoundingMode.UP);
            integralHistoryInfo.setMoney(divide);
        }

        //2、判断用户积分使用类型
        IntegralInfo info = new IntegralInfo();
        info.setUserId(integralHistoryInfo.getUserId());
        BigDecimal zero = BigDecimal.valueOf(0);
        //充值和支出，逻辑都是一样为用户添加积分
        if (integralHistoryInfo.getType().equals(WS_INTEGRAL_TYPE_0)) {
            info.setIntegral(integralHistoryInfo.getIntegral());
//            info.setResidueInteral(zero);
            //负数 后面是添加
            integralHistoryInfo.setStatus(WS_INTEGRAL_STATUS_1);

            //获取用户积分是否积分不足，如果不足不可以充值
            IntegralInfo integralInfo = new IntegralInfo();
            integralInfo.setUserId(SecurityUtils.getUserId());
            List<IntegralInfo> integralInfos = integralInfoService.selectIntegralInfoList(integralInfo);
            if (StringUtils.isEmpty(integralInfos) || integralInfos.get(0).getIntegral().compareTo(integralHistoryInfo.getIntegral()) < 0) {
                throw new ServiceException("您的积分不足！！！");
            }
            //执行为充值的用户支出积分
            IntegralHistoryInfo insertHistoryInfoUser = new IntegralHistoryInfo();
            insertHistoryInfoUser.setUserId(SecurityUtils.getUserId());
            insertHistoryInfoUser.setRemark("为用户（" + integralHistoryInfo.getUserId() + "）充值积分：" + integralHistoryInfo.getIntegral());
            insertHistoryInfoUser.setType(WS_INTEGRAL_TYPE_3);
            insertHistoryInfoUser.setIntegral(integralHistoryInfo.getIntegral());
            insertIntegralHistoryInfo(insertHistoryInfoUser);
        }
        //冻结
        else if (integralHistoryInfo.getType().equals(WS_INTEGRAL_TYPE_1)) {
            //更新用户积分
            info.setIntegral(zero.subtract(integralHistoryInfo.getIntegral()));
            info.setFreezeIntegral(integralHistoryInfo.getIntegral());
            integralHistoryInfo.setStatus(WS_INTEGRAL_STATUS_1);
        }
        //退还
        else if (integralHistoryInfo.getType().equals(WS_INTEGRAL_TYPE_2)) {
            info.setIntegral(integralHistoryInfo.getIntegral());
//            info.setResidueInteral(zero);
            //负数 后面是添加
            info.setIntegral(integralHistoryInfo.getIntegral());
            info.setFreezeIntegral(zero.subtract(integralHistoryInfo.getIntegral()));
            System.err.println("info = " + info.getIntegral());
            integralHistoryInfo.setStatus(WS_INTEGRAL_STATUS_1);
        }
        //支出
        else {
            integralHistoryInfo.setStatus(WS_INTEGRAL_STATUS_1);
            //负数 后面是添加
            info.setIntegral(zero.subtract(integralHistoryInfo.getIntegral()));
            integralHistoryInfo.setIntegral(zero.subtract(integralHistoryInfo.getIntegral()));
        }
        //插入记录，更新
        integralInfoService.insertOrUpdateIntegralInfo(info, integralHistoryInfo);

        SysUser sysUser = userService.selectUserById(integralHistoryInfo.getUserId());
        integralHistoryInfo.setDeptId(sysUser.getDeptId());
        try {
            integralHistoryInfo.setCreateBy(SecurityUtils.getUsername());
        } catch (Exception e) {
            integralHistoryInfo.setCreateBy("系统自动创建");
        }
        integralHistoryInfo.setId(IdUtils.fastSimpleUUID());
        integralHistoryInfo.setCreateTime(DateUtils.getNowDate());
        integralHistoryInfo.setIsDelete(WS_IS_DELETE_0);
        return integralHistoryInfoMapper.insertIntegralHistoryInfo(integralHistoryInfo);
    }

    /**
     * 修改积分记录信息
     *
     * @param integralHistoryInfo 积分记录信息
     * @return 结果
     */
    @Override
    public int updateIntegralHistoryInfo(IntegralHistoryInfo integralHistoryInfo) {
        integralHistoryInfo.setUpdateTime(DateUtils.getNowDate());
        return integralHistoryInfoMapper.updateIntegralHistoryInfo(integralHistoryInfo);
    }

    /**
     * 批量删除积分记录信息
     *
     * @param ids 需要删除的积分记录信息主键
     * @return 结果
     */
    @Override
    public int deleteIntegralHistoryInfoByIds(String[] ids) {
        return integralHistoryInfoMapper.deleteIntegralHistoryInfoByIds(ids);
    }

    /**
     * 删除积分记录信息信息
     *
     * @param id 积分记录信息主键
     * @return 结果
     */
    @Override
    public int deleteIntegralHistoryInfoById(String id) {
        return integralHistoryInfoMapper.deleteIntegralHistoryInfoById(id);
    }
}

package com.yy.manage.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.yy.common.annotation.DataScope;
import com.yy.common.core.domain.entity.SysDept;
import com.yy.common.core.domain.entity.SysUser;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.IntegralHistoryInfo;
import com.yy.system.service.ISysDeptService;
import com.yy.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.IntegralInfoMapper;
import com.yy.manage.domain.IntegralInfo;
import com.yy.manage.service.IIntegralInfoService;

import static com.yy.common.constant.DictDataConstants.WS_IS_DELETE_0;

/**
 * 积分信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-06
 */
@Service
public class IntegralInfoServiceImpl implements IIntegralInfoService {
    @Autowired
    private IntegralInfoMapper integralInfoMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询积分信息
     *
     * @param id 积分信息主键
     * @return 积分信息
     */
    @Override
    public IntegralInfo selectIntegralInfoById(String id) {
        return integralInfoMapper.selectIntegralInfoById(id);
    }

    /**
     * 查询积分信息列表
     *
     * @param integralInfo 积分信息
     * @return 积分信息
     */
    @DataScope(deptAlias = "tb_integral_info",userAlias ="tb_integral_info")
    @Override
    public List<IntegralInfo> selectIntegralInfoList(IntegralInfo integralInfo) {
        List<IntegralInfo> integralInfos = integralInfoMapper.selectIntegralInfoList(integralInfo);
        for (IntegralInfo info : integralInfos) {
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
        return integralInfos;
    }

    /**
     * 新增积分信息
     *
     * @param integralInfo 积分信息
     * @return 结果
     */
    @Override
    public int insertIntegralInfo(IntegralInfo integralInfo) {
        SysUser sysUser = userService.selectUserById(integralInfo.getUserId());
        integralInfo.setFreezeIntegral(BigDecimal.valueOf(0));
        integralInfo.setDeptId(sysUser.getDeptId());
        integralInfo.setId(IdUtils.fastSimpleUUID());
        integralInfo.setCreateBy(SecurityUtils.getUsername());
        integralInfo.setIsDelete(WS_IS_DELETE_0);
        integralInfo.setCreateTime(DateUtils.getNowDate());
        return integralInfoMapper.insertIntegralInfo(integralInfo);
    }

    /**
     * 修改积分信息
     *
     * @param integralInfo 积分信息
     * @return 结果
     */
    @Override
    public int updateIntegralInfo(IntegralInfo integralInfo) {
        integralInfo.setUpdateTime(DateUtils.getNowDate());
        return integralInfoMapper.updateIntegralInfo(integralInfo);
    }

    /**
     * 批量删除积分信息
     *
     * @param ids 需要删除的积分信息主键
     * @return 结果
     */
    @Override
    public int deleteIntegralInfoByIds(String[] ids) {
        return integralInfoMapper.deleteIntegralInfoByIds(ids);
    }

    /**
     * 删除积分信息信息
     *
     * @param id 积分信息主键
     * @return 结果
     */
    @Override
    public int deleteIntegralInfoById(String id) {
        return integralInfoMapper.deleteIntegralInfoById(id);
    }

    /**
     * @description:
     * @author: YY
     * @method: insertOrUpdateIntegralInfo
     * @date: 2024/6/6 17:39
     * @param:
     * @param: info
     * @param: integralHistoryInfo
     * @return: void
     **/

    @Override
    public void insertOrUpdateIntegralInfo(IntegralInfo integralInfo, IntegralHistoryInfo integralHistoryInfo) {
        //查询条件
        IntegralInfo info = new IntegralInfo();
        info.setUserId(integralInfo.getUserId());
        List<IntegralInfo> integralInfos = integralInfoMapper.selectIntegralInfoList(info);
        //判断是否为空
        if (StringUtils.isNotEmpty(integralInfos)) {
            //不为空则更新
            IntegralInfo old = integralInfos.get(0);
            IntegralInfo updateIntegralInfo = new IntegralInfo();
            updateIntegralInfo.setId(old.getId());
            updateIntegralInfo.setIntegral(old.getIntegral().add(integralInfo.getIntegral()));
            //判断冻结积分是否为空
            if (StringUtils.isNotNull(integralInfo.getFreezeIntegral())) {
                updateIntegralInfo.setFreezeIntegral(old.getFreezeIntegral().add(integralInfo.getFreezeIntegral()));
                //判断冻结积分是否小于0
                BigDecimal zero = BigDecimal.valueOf(0);
                int i = updateIntegralInfo.getFreezeIntegral().compareTo(zero);
                //如果小于0则更新
                /*
                 * 如果被比较的 `BigDecimal` 在数值上小于此 `BigDecimal`，则返回值小于0。
                 * 如果被比较的 `BigDecimal` 在数值上等于此 `BigDecimal`，则返回值等于0。
                 * 如果被比较的 `BigDecimal` 在数值上大于此 `BigDecimal`，则返回值大于0
                 * */
                if (i < 0) {
                    updateIntegralInfo.setFreezeIntegral(zero);
                }
            }
            updateIntegralInfo.setUpdateTime(new Date());
            integralInfoMapper.updateIntegralInfo(updateIntegralInfo);

            //为使用记录赋值
            integralHistoryInfo.setEndIntegral(updateIntegralInfo.getIntegral());
            integralHistoryInfo.setStartIntegral(old.getIntegral());
        } else {
            insertIntegralInfo(integralInfo);
            integralHistoryInfo.setEndIntegral(integralInfo.getIntegral());
            integralHistoryInfo.setStartIntegral(BigDecimal.valueOf(0));
        }

    }
}

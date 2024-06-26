package com.yy.manage.service.impl;

import java.util.List;

import com.yy.common.annotation.DataScope;
import com.yy.common.core.domain.entity.SysDept;
import com.yy.common.core.domain.entity.SysUser;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.OrderInfo;
import com.yy.manage.service.IOrderInfoService;
import com.yy.system.service.ISysDeptService;
import com.yy.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.OperationHistoryInfoMapper;
import com.yy.manage.domain.OperationHistoryInfo;
import com.yy.manage.service.IOperationHistoryInfoService;

/**
 * 操作记录Service业务层处理
 *
 * @author yy
 * @date 2024-06-07
 */
@Service
public class OperationHistoryInfoServiceImpl implements IOperationHistoryInfoService {
    @Autowired
    private OperationHistoryInfoMapper operationHistoryInfoMapper;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 查询操作记录
     *
     * @param id 操作记录主键
     * @return 操作记录
     */
    @Override
    public OperationHistoryInfo selectOperationHistoryInfoById(String id) {
        return operationHistoryInfoMapper.selectOperationHistoryInfoById(id);
    }

    /**
     * 查询操作记录列表
     *
     * @param operationHistoryInfo 操作记录
     * @return 操作记录
     */
    @DataScope(deptAlias = "tb_operation_history_info",userAlias ="tb_operation_history_info")
    @Override
    public List<OperationHistoryInfo> selectOperationHistoryInfoList(OperationHistoryInfo operationHistoryInfo) {
        List<OperationHistoryInfo> operationHistoryInfos = operationHistoryInfoMapper.selectOperationHistoryInfoList(operationHistoryInfo);
        for (OperationHistoryInfo info : operationHistoryInfos) {
            SysUser sysUser = userService.selectUserById(info.getUserId());
            if (StringUtils.isNotNull(sysUser)) {
                info.setUserName(sysUser.getUserName());
            }
            SysDept sysDept = deptService.selectDeptById(info.getDeptId());
            if (StringUtils.isNotNull(sysDept)) {
                info.setDeptName(sysDept.getDeptName());
                info.setAgencyUserName(sysDept.getLeader());
            }
            OrderInfo orderInfo = orderInfoService.selectOrderInfoById(info.getOrderId());
            if (StringUtils.isNotNull(orderInfo)) {
                info.setOrderName(orderInfo.getName());
            }
        }
        return operationHistoryInfos;
    }

    /**
     * 新增操作记录
     *
     * @param operationHistoryInfo 操作记录
     * @return 结果
     */
    @Override
    public int insertOperationHistoryInfo(OperationHistoryInfo operationHistoryInfo) {
        if (StringUtils.isNull(operationHistoryInfo.getDeptId())) {
            operationHistoryInfo.setDeptId(SecurityUtils.getDeptId());
        }
        operationHistoryInfo.setId(IdUtils.fastSimpleUUID());
        if (StringUtils.isNull(operationHistoryInfo.getCreateBy())) {
            operationHistoryInfo.setCreateBy(SecurityUtils.getUsername());
        }
        if (StringUtils.isNull(operationHistoryInfo.getUserId())) {
            operationHistoryInfo.setUserId(SecurityUtils.getUserId());
        }
        operationHistoryInfo.setCreateTime(DateUtils.getNowDate());
        return operationHistoryInfoMapper.insertOperationHistoryInfo(operationHistoryInfo);
    }

    /**
     * 修改操作记录
     *
     * @param operationHistoryInfo 操作记录
     * @return 结果
     */
    @Override
    public int updateOperationHistoryInfo(OperationHistoryInfo operationHistoryInfo) {
        return operationHistoryInfoMapper.updateOperationHistoryInfo(operationHistoryInfo);
    }

    /**
     * 批量删除操作记录
     *
     * @param ids 需要删除的操作记录主键
     * @return 结果
     */
    @Override
    public int deleteOperationHistoryInfoByIds(String[] ids) {
        return operationHistoryInfoMapper.deleteOperationHistoryInfoByIds(ids);
    }

    /**
     * 删除操作记录信息
     *
     * @param id 操作记录主键
     * @return 结果
     */
    @Override
    public int deleteOperationHistoryInfoById(String id) {
        return operationHistoryInfoMapper.deleteOperationHistoryInfoById(id);
    }
}

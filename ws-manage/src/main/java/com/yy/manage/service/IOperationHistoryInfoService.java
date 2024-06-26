package com.yy.manage.service;

import java.util.List;
import com.yy.manage.domain.OperationHistoryInfo;

/**
 * 操作记录Service接口
 * 
 * @author yy
 * @date 2024-06-07
 */
public interface IOperationHistoryInfoService 
{
    /**
     * 查询操作记录
     * 
     * @param id 操作记录主键
     * @return 操作记录
     */
    public OperationHistoryInfo selectOperationHistoryInfoById(String id);

    /**
     * 查询操作记录列表
     * 
     * @param operationHistoryInfo 操作记录
     * @return 操作记录集合
     */
    public List<OperationHistoryInfo> selectOperationHistoryInfoList(OperationHistoryInfo operationHistoryInfo);

    /**
     * 新增操作记录
     * 
     * @param operationHistoryInfo 操作记录
     * @return 结果
     */
    public int insertOperationHistoryInfo(OperationHistoryInfo operationHistoryInfo);

    /**
     * 修改操作记录
     * 
     * @param operationHistoryInfo 操作记录
     * @return 结果
     */
    public int updateOperationHistoryInfo(OperationHistoryInfo operationHistoryInfo);

    /**
     * 批量删除操作记录
     * 
     * @param ids 需要删除的操作记录主键集合
     * @return 结果
     */
    public int deleteOperationHistoryInfoByIds(String[] ids);

    /**
     * 删除操作记录信息
     * 
     * @param id 操作记录主键
     * @return 结果
     */
    public int deleteOperationHistoryInfoById(String id);
}

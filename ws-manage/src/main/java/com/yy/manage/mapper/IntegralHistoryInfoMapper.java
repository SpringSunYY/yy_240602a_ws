package com.yy.manage.mapper;

import java.util.List;
import com.yy.manage.domain.IntegralHistoryInfo;

/**
 * 积分记录信息Mapper接口
 * 
 * @author yy
 * @date 2024-06-06
 */
public interface IntegralHistoryInfoMapper 
{
    /**
     * 查询积分记录信息
     * 
     * @param id 积分记录信息主键
     * @return 积分记录信息
     */
    public IntegralHistoryInfo selectIntegralHistoryInfoById(String id);

    /**
     * 查询积分记录信息列表
     * 
     * @param integralHistoryInfo 积分记录信息
     * @return 积分记录信息集合
     */
    public List<IntegralHistoryInfo> selectIntegralHistoryInfoList(IntegralHistoryInfo integralHistoryInfo);

    /**
     * 新增积分记录信息
     * 
     * @param integralHistoryInfo 积分记录信息
     * @return 结果
     */
    public int insertIntegralHistoryInfo(IntegralHistoryInfo integralHistoryInfo);

    /**
     * 修改积分记录信息
     * 
     * @param integralHistoryInfo 积分记录信息
     * @return 结果
     */
    public int updateIntegralHistoryInfo(IntegralHistoryInfo integralHistoryInfo);

    /**
     * 删除积分记录信息
     * 
     * @param id 积分记录信息主键
     * @return 结果
     */
    public int deleteIntegralHistoryInfoById(String id);

    /**
     * 批量删除积分记录信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteIntegralHistoryInfoByIds(String[] ids);
}

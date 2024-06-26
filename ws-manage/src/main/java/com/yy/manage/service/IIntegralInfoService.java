package com.yy.manage.service;

import java.util.List;

import com.yy.manage.domain.IntegralHistoryInfo;
import com.yy.manage.domain.IntegralInfo;

/**
 * 积分信息Service接口
 *
 * @author yy
 * @date 2024-06-06
 */
public interface IIntegralInfoService {
    /**
     * 查询积分信息
     *
     * @param id 积分信息主键
     * @return 积分信息
     */
    public IntegralInfo selectIntegralInfoById(String id);

    /**
     * 查询积分信息列表
     *
     * @param integralInfo 积分信息
     * @return 积分信息集合
     */
    public List<IntegralInfo> selectIntegralInfoList(IntegralInfo integralInfo);

    /**
     * 新增积分信息
     *
     * @param integralInfo 积分信息
     * @return 结果
     */
    public int insertIntegralInfo(IntegralInfo integralInfo);

    /**
     * 修改积分信息
     *
     * @param integralInfo 积分信息
     * @return 结果
     */
    public int updateIntegralInfo(IntegralInfo integralInfo);

    /**
     * 批量删除积分信息
     *
     * @param ids 需要删除的积分信息主键集合
     * @return 结果
     */
    public int deleteIntegralInfoByIds(String[] ids);

    /**
     * 删除积分信息信息
     *
     * @param id 积分信息主键
     * @return 结果
     */
    public int deleteIntegralInfoById(String id);

    /**
     * @description:插入或者更新用户积分，并且为积分使用记录赋值
     * @author: YY
     * @method: insertOrUpdateIntegralInfo
     * @date: 2024/6/6 17:35
     * @param: [info, integralHistoryInfo]
     * @return: void
     **/
    void insertOrUpdateIntegralInfo(IntegralInfo info, IntegralHistoryInfo integralHistoryInfo);
}

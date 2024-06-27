package com.yy.manage.service;

import java.util.List;
import com.yy.manage.domain.WsUpstreamOrderInfo;

/**
 * ws上游订单信息Service接口
 * 
 * @author yy
 * @date 2024-06-27
 */
public interface IWsUpstreamOrderInfoService 
{
    /**
     * 查询ws上游订单信息
     * 
     * @param id ws上游订单信息主键
     * @return ws上游订单信息
     */
    public WsUpstreamOrderInfo selectWsUpstreamOrderInfoById(String id);

    /**
     * 查询ws上游订单信息列表
     * 
     * @param wsUpstreamOrderInfo ws上游订单信息
     * @return ws上游订单信息集合
     */
    public List<WsUpstreamOrderInfo> selectWsUpstreamOrderInfoList(WsUpstreamOrderInfo wsUpstreamOrderInfo);

    /**
     * 新增ws上游订单信息
     * 
     * @param wsUpstreamOrderInfo ws上游订单信息
     * @return 结果
     */
    public int insertWsUpstreamOrderInfo(WsUpstreamOrderInfo wsUpstreamOrderInfo);

    /**
     * 修改ws上游订单信息
     * 
     * @param wsUpstreamOrderInfo ws上游订单信息
     * @return 结果
     */
    public int updateWsUpstreamOrderInfo(WsUpstreamOrderInfo wsUpstreamOrderInfo);

    /**
     * 批量删除ws上游订单信息
     * 
     * @param ids 需要删除的ws上游订单信息主键集合
     * @return 结果
     */
    public int deleteWsUpstreamOrderInfoByIds(String[] ids);

    /**
     * 删除ws上游订单信息信息
     * 
     * @param id ws上游订单信息主键
     * @return 结果
     */
    public int deleteWsUpstreamOrderInfoById(String id);
}

package com.yy.manage.mapper;

import java.util.List;

import com.yy.manage.domain.WsOrderInfo;

/**
 * WS订单信息Mapper接口
 *
 * @author yy
 * @date 2024-06-13
 */
public interface WsOrderInfoMapper {
    /**
     * 查询WS订单信息
     *
     * @param id WS订单信息主键
     * @return WS订单信息
     */
    public WsOrderInfo selectWsOrderInfoById(String id);

    /**
     * 查询WS订单信息列表
     *
     * @param wsOrderInfo WS订单信息
     * @return WS订单信息集合
     */
    public List<WsOrderInfo> selectWsOrderInfoList(WsOrderInfo wsOrderInfo);

    /**
     * @description: 查询不等于这个状态且未删除数据
     * @author: YY
     * @method: selectWithNoStatusWsOrderInfoList
     * @date: 2024/6/14 18:33
     * @param:
     * @param: wsOrderInfo
     * @return: java.util.List<com.yy.manage.domain.WsOrderInfo>
     **/
    public List<WsOrderInfo> selectWithNoStatusWsOrderInfoList(WsOrderInfo wsOrderInfo);

    /**
     * 新增WS订单信息
     *
     * @param wsOrderInfo WS订单信息
     * @return 结果
     */
    public int insertWsOrderInfo(WsOrderInfo wsOrderInfo);

    /**
     * 修改WS订单信息
     *
     * @param wsOrderInfo WS订单信息
     * @return 结果
     */
    public int updateWsOrderInfo(WsOrderInfo wsOrderInfo);

    /**
     * 删除WS订单信息
     *
     * @param id WS订单信息主键
     * @return 结果
     */
    public int deleteWsOrderInfoById(String id);

    /**
     * 批量删除WS订单信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWsOrderInfoByIds(String[] ids);

    List<WsOrderInfo> selectWithStatusNo4ANd5WsOrderInfoList();
}

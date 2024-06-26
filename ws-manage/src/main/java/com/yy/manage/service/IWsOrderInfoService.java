package com.yy.manage.service;

import java.util.List;

import com.yy.manage.domain.WsOrderInfo;

/**
 * WS订单信息Service接口
 *
 * @author yy
 * @date 2024-06-13
 */
public interface IWsOrderInfoService {
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
     * 批量删除WS订单信息
     *
     * @param ids 需要删除的WS订单信息主键集合
     * @return 结果
     */
    public int deleteWsOrderInfoByIds(String[] ids);

    /**
     * 删除WS订单信息信息
     *
     * @param id WS订单信息主键
     * @return 结果
     */
    public int deleteWsOrderInfoById(String id);

    WsOrderInfo computeIntegral(WsOrderInfo orderInfo);

    /**
     * @description: 查询订单状态并更新订单
     * @author: YY
     * @method: getTaskStatusWithUpdateWsOrderStatus
     * @date: 2024/6/14 18:35
     * @param:
     * @return: int
     **/
    int getTaskStatusWithUpdateWsOrderStatus(WsOrderInfo wsOrderInfo);

    /**
     * @description: 查询是否创建任务
     * @author: YY
     * @method: getIsCreateTask
     * @date: 2024/6/19 16:22
     * @param:
     * @return: int
     **/
    int getIsCreateTask();

    /**
     * @description: 定时更新订单信息
     * @author: YY
     * @method: wsTaskUpdateTask
     * @date: 2024/6/20 14:15
     * @param:
     * @return: void
     **/
    void wsTaskUpdateTask();

    /**
     * @description: 更新发送时间
     * @author: YY
     * @method: updateSendTime
     * @date: 2024/6/26 21:12
     * @param:
     * @param: wsOrderInfo
     * @return: int
     **/
    int updateSendTime(WsOrderInfo wsOrderInfo);

    /**
     * @description: 修改订单发送状态
     * @author: YY
     * @method: updateSendStatus
     * @date: 2024/6/26 22:23
     * @param:
     * @param: wsOrderInfo
     * @return: int
     **/
    int updateSendStatus(WsOrderInfo wsOrderInfo);
}

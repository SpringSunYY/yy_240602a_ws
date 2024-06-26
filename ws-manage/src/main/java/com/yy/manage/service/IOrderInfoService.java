package com.yy.manage.service;

import java.util.List;

import com.yy.manage.domain.OrderInfo;

/**
 * 订单信息Service接口
 *
 * @author yy
 * @date 2024-06-07
 */
public interface IOrderInfoService {
    /**
     * 查询订单信息
     *
     * @param id 订单信息主键
     * @return 订单信息
     */
    public OrderInfo selectOrderInfoById(String id);

    /**
     * 查询订单信息列表
     *
     * @param orderInfo 订单信息
     * @return 订单信息集合
     */
    public List<OrderInfo> selectOrderInfoList(OrderInfo orderInfo);

    /**
     * 新增订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    public int insertOrderInfo(OrderInfo orderInfo);

    /**
     * 修改订单信息
     *
     * @param orderInfo 订单信息
     * @return 结果
     */
    public int updateOrderInfo(OrderInfo orderInfo);

    /**
     * 批量删除订单信息
     *
     * @param ids 需要删除的订单信息主键集合
     * @return 结果
     */
    public int deleteOrderInfoByIds(String[] ids);

    /**
     * 删除订单信息信息
     *
     * @param id 订单信息主键
     * @return 结果
     */
    public int deleteOrderInfoById(String id);

    /**
     * @description: 计算文件所用积分
     * @author: YY
     * @method: computeIntegral
     * @date: 2024/6/7 22:20
     * @param:
     * @param: orderInfo
     * @return: com.yy.manage.domain.OrderInfo
     **/
    OrderInfo computeIntegral(OrderInfo orderInfo);

    /**
     * @description: 优化订单
     * @author: YY
     * @method: optimize
     * @date: 2024/6/8 16:33
     * @param:
     * @param: orderInfo
     * @return: int
     **/
    int optimize();
}

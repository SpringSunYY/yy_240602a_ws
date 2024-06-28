package com.yy.manage.service.impl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson2.JSON;
import com.yy.common.core.redis.RedisCache;
import com.yy.common.exception.ServiceException;
import com.yy.common.utils.DateUtils;
import com.yy.common.utils.SecurityUtils;
import com.yy.common.utils.StringUtils;
import com.yy.common.utils.uuid.IdUtils;
import com.yy.manage.domain.WsOrderInfo;
import com.yy.manage.domain.vo.ReturnTaskVo;
import org.json.JSONArray;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.yy.manage.mapper.WsUpstreamOrderInfoMapper;
import com.yy.manage.domain.WsUpstreamOrderInfo;
import com.yy.manage.service.IWsUpstreamOrderInfoService;

import static com.yy.common.constant.ApiReturnConstants.RETURN_STATUS_ERR;
import static com.yy.common.constant.ConfigConstants.*;
import static com.yy.common.constant.DictDataConstants.WS_TASK_STATUS_0;
import static com.yy.common.constant.DictDataConstants.WS_UPSTREAM_TASK_STATUS_0;
import static com.yy.manage.utils.HttpGetUtil.sendJsonByGetReq;

/**
 * ws上游订单信息Service业务层处理
 *
 * @author yy
 * @date 2024-06-27
 */
@Service
public class WsUpstreamOrderInfoServiceImpl implements IWsUpstreamOrderInfoService {
    @Autowired
    private WsUpstreamOrderInfoMapper wsUpstreamOrderInfoMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询ws上游订单信息
     *
     * @param id ws上游订单信息主键
     * @return ws上游订单信息
     */
    @Override
    public WsUpstreamOrderInfo selectWsUpstreamOrderInfoById(String id) {
        return wsUpstreamOrderInfoMapper.selectWsUpstreamOrderInfoById(id);
    }

    /**
     * 查询ws上游订单信息列表
     *
     * @param wsUpstreamOrderInfo ws上游订单信息
     * @return ws上游订单信息
     */
    @Override
    public List<WsUpstreamOrderInfo> selectWsUpstreamOrderInfoList(WsUpstreamOrderInfo wsUpstreamOrderInfo) {
        return wsUpstreamOrderInfoMapper.selectWsUpstreamOrderInfoList(wsUpstreamOrderInfo);
    }

    /**
     * 新增ws上游订单信息
     *
     * @param wsUpstreamOrderInfo ws上游订单信息
     * @return 结果
     */
    @Override
    public int insertWsUpstreamOrderInfo(WsUpstreamOrderInfo wsUpstreamOrderInfo) {
        wsUpstreamOrderInfo.setCreateBy(SecurityUtils.getUsername());
        wsUpstreamOrderInfo.setCreateTime(DateUtils.getNowDate());
        wsUpstreamOrderInfo.setIsUse(WS_UPSTREAM_TASK_STATUS_0);
        batchCreateWsUpstreamOrderInfo(wsUpstreamOrderInfo);
        return 1;
    }

    /**
     * @description: 批量创建订单
     * @author: YY
     * @method: batchCreateWsUpstreamOrderInfo
     * @date: 2024/6/27 22:27
     * @param:
     * @param: wsUpstreamOrderInfo
     * @return: void
     **/
    private void batchCreateWsUpstreamOrderInfo(WsUpstreamOrderInfo wsUpstreamOrderInfo) {
        String string = wsUpstreamOrderInfo.getTaskId();
        if (StringUtils.isNull(string)) {
            throw new ServiceException("taskId有误！！！");
        }
        String[] lines = string.split("\n");
        //得到所有订单，遍历每个订单是否存在或者已经被使用
        Set<String> hashSet = new HashSet<>(Arrays.asList(lines));
        //获取基本信息 url apiToken userId
        String apiToken = redisCache.getCacheConfig(WS_API_TOKEN);
        String userId = redisCache.getCacheConfig(WS_USER_ID);
        String taskInfoUrl = redisCache.getCacheConfig(WS_TASK_INFO_URL);

        //多线程创建
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (String taskId : hashSet) {
            executorService.submit(() -> {
                //如果返回ture则创建订单信息
                String type = wsTaskGetTaskInfo(taskId, taskInfoUrl, userId, apiToken);
                if (StringUtils.isNotNull(type)) {
                    //判断数据库是否已经有了此taskId，如果有则不可再创建数据
                    WsUpstreamOrderInfo orderInfo = new WsUpstreamOrderInfo();
                    orderInfo.setTaskId(taskId);
                    if (StringUtils.isNotEmpty(wsUpstreamOrderInfoMapper.selectWsUpstreamOrderInfoList(orderInfo))) {
                        return;
                    }

                    WsUpstreamOrderInfo upstreamOrderInfo = new WsUpstreamOrderInfo();
                    BeanUtils.copyProperties(wsUpstreamOrderInfo, upstreamOrderInfo);
                    upstreamOrderInfo.setTaskId(taskId);
                    upstreamOrderInfo.setName(upstreamOrderInfo.getName() + "-" + taskId);
                    upstreamOrderInfo.setSendType(type);
                    System.out.println("插入订单");
                    upstreamOrderInfo.setUseOrderId(null);
                    upstreamOrderInfo.setId(IdUtils.fastSimpleUUID());
                    wsUpstreamOrderInfoMapper.insertWsUpstreamOrderInfo(upstreamOrderInfo);
                }
            });
        }
    }

    /**
     * @description: 查询订单状态是否为仅保存
     * @author: YY
     * @method: wsTaskGetTaskInfo
     * @date: 2024/6/27 21:59
     * @param:
     * @param: taskId
     * @param: url
     * @param: userId
     * @param: apiToken
     * @return: boolean
     **/
    private String wsTaskGetTaskInfo(String taskId, String url, String userId, String apiToken) {

        if (StringUtils.isNull(taskId)) {
            return null;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("api_token", apiToken);
        map.put("user_id", userId);
        String reqParams = JSON.toJSONString(map);
        try {
            //System.out.println("taskId = " + taskId);
            String s = sendJsonByGetReq(url + "/" + taskId, reqParams, "UTF-8");
            ReturnTaskVo returnTaskVo = JSON.parseObject(s, ReturnTaskVo.class);
            //判断是否返回错误，错误则返回false
            if (returnTaskVo.getStatus().equals(RETURN_STATUS_ERR)) {
                return null;
            }
            //获取订单状态，如果是仅保存则返回true，如果是不是则返回false
            if (returnTaskVo.getTask_info().getStatus().equals(WS_TASK_STATUS_0)) {
                return returnTaskVo.getTask_info().getTask_type();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 修改ws上游订单信息
     *
     * @param wsUpstreamOrderInfo ws上游订单信息
     * @return 结果
     */
    @Override
    public int updateWsUpstreamOrderInfo(WsUpstreamOrderInfo wsUpstreamOrderInfo) {
        wsUpstreamOrderInfo.setUpdateTime(DateUtils.getNowDate());
        return wsUpstreamOrderInfoMapper.updateWsUpstreamOrderInfo(wsUpstreamOrderInfo);
    }

    /**
     * 批量删除ws上游订单信息
     *
     * @param ids 需要删除的ws上游订单信息主键
     * @return 结果
     */
    @Override
    public int deleteWsUpstreamOrderInfoByIds(String[] ids) {
        return wsUpstreamOrderInfoMapper.deleteWsUpstreamOrderInfoByIds(ids);
    }

    /**
     * 删除ws上游订单信息信息
     *
     * @param id ws上游订单信息主键
     * @return 结果
     */
    @Override
    public int deleteWsUpstreamOrderInfoById(String id) {
        return wsUpstreamOrderInfoMapper.deleteWsUpstreamOrderInfoById(id);
    }
}

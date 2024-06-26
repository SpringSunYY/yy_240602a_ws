package com.yy.quartz.task;

import com.yy.manage.service.IWsOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Project: WS
 * @Package: com.yy.quartz.task
 * @Author: YY
 * @CreateTime: 2024-06-20  15:09
 * @Description: WsTask
 * @Version: 1.0
 */
@Component("wsTask")
public class WsTask {

    @Autowired
    private IWsOrderInfoService wsOrderInfoService;

    /**
     * 定时更新订单
     */
   public void wsTaskGetTaskInfo() {
        System.out.println("定时更新订单" + System.currentTimeMillis());
        // 定时更新订单
        wsOrderInfoService.wsTaskUpdateTask();
    }
}

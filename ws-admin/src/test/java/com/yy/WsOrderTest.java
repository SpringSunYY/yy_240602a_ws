package com.yy;

import com.yy.manage.domain.WsOrderInfo;
import com.yy.manage.service.IWsOrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Project: WS
 * @Package: com.yy
 * @Author: YY
 * @CreateTime: 2024-06-16  15:26
 * @Description: WsOrderTest
 * @Version: 1.0
 */
@SpringBootTest
public class WsOrderTest {
    @Autowired
    public IWsOrderInfoService wsOrderInfoService;

    @Test
    public void updateTest(){
        WsOrderInfo wsOrderInfo = new WsOrderInfo();
        wsOrderInfo.setId("d64d821b62ab4874879c3ae3da3908a2");
        wsOrderInfo.setTaskId("92108");
        wsOrderInfo.setName("测试0617U");
        wsOrderInfo.setCountryId("87e9e70907984e66aa000ffcf335cc51");
        wsOrderInfo.setSendType("3");
        wsOrderInfo.setPrices(BigDecimal.valueOf(59.43));
        wsOrderInfo.setOrderNumber(4627L);
        wsOrderInfo.setActualNumber(4160L);
        wsOrderInfo.setOptimizedNumber(467L);
        wsOrderInfo.setAccomplishNumber(3000L);
        wsOrderInfo.setUseIntegral(BigDecimal.valueOf(4627.00));
        wsOrderInfo.setActualIntegral(BigDecimal.valueOf(4160.00));
        wsOrderInfo.setFileContent("/profile/upload/2024/06/16/res_succ_90260_20240616153549A001.txt");
        wsOrderInfo.setFileFilter("/profile/upload/2024/06/16\\filter\\res_succ_90260_20240616153549A001.txt");
        wsOrderInfo.setCopyContent("你好{url}");
        wsOrderInfo.setLinkContent("www.baidu.com");
//        wsOrderInfo.setServicePhone();
        wsOrderInfo.setButtons1("A");
        wsOrderInfo.setLinks1("A");
        wsOrderInfo.setSendTime(new Date());
        wsOrderInfo.setEndTime(new Date());
        wsOrderInfo.setStatus("5");
        wsOrderInfo.setUserId(103L);
        wsOrderInfo.setDeptId(200L);
        wsOrderInfoService.updateWsOrderInfo(wsOrderInfo);
    }
}

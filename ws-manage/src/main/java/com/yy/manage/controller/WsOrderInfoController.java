package com.yy.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.yy.manage.domain.OrderInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.yy.common.annotation.Log;
import com.yy.common.core.controller.BaseController;
import com.yy.common.core.domain.AjaxResult;
import com.yy.common.enums.BusinessType;
import com.yy.manage.domain.WsOrderInfo;
import com.yy.manage.service.IWsOrderInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * WS订单信息Controller
 * 
 * @author yy
 * @date 2024-06-13
 */
@RestController
@RequestMapping("/manage/wsOrderInfo")
public class WsOrderInfoController extends BaseController
{
    @Autowired
    private IWsOrderInfoService wsOrderInfoService;

    /**
     * 查询WS订单信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(WsOrderInfo wsOrderInfo)
    {
        startPage();
        List<WsOrderInfo> list = wsOrderInfoService.selectWsOrderInfoList(wsOrderInfo);
        return getDataTable(list);
    }

    /**
     * 导出WS订单信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:export')")
    @Log(title = "WS订单信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WsOrderInfo wsOrderInfo)
    {
        List<WsOrderInfo> list = wsOrderInfoService.selectWsOrderInfoList(wsOrderInfo);
        ExcelUtil<WsOrderInfo> util = new ExcelUtil<WsOrderInfo>(WsOrderInfo.class);
        util.exportExcel(response, list, "WS订单信息数据");
    }

    /**
     * 获取WS订单信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wsOrderInfoService.selectWsOrderInfoById(id));
    }

    /**
     * 新增WS订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:add')")
    @Log(title = "WS订单信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WsOrderInfo wsOrderInfo)
    {
        return toAjax(wsOrderInfoService.insertWsOrderInfo(wsOrderInfo));
    }

    /**
     * 修改WS订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:edit')")
    @Log(title = "WS订单信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WsOrderInfo wsOrderInfo)
    {
        return toAjax(wsOrderInfoService.updateWsOrderInfo(wsOrderInfo));
    }

    /**
     * 删除WS订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:remove')")
    @Log(title = "WS订单信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wsOrderInfoService.deleteWsOrderInfoByIds(ids));
    }

    @GetMapping("/compute")
    public AjaxResult computeIntegral(WsOrderInfo orderInfo) {

        return success(wsOrderInfoService.computeIntegral(orderInfo));
    }


    /**
     * @description: 查询订单状态并更新订单
     * @author: YY
     * @method: getTaskStatusWithUpdateWsOrderStatus
     * @date: 2024/6/14 18:35
     * @param:
     * @return: int
     **/
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:updateStatus')")
    @PostMapping("/updateStatus")
    public AjaxResult getTaskStatusWithUpdateWsOrderStatus(WsOrderInfo wsOrderInfo) {
        return success(wsOrderInfoService.getTaskStatusWithUpdateWsOrderStatus(wsOrderInfo));
    }

    /**
     * @description: 判断是否可创建订单
     * @author: YY
     * @method: getIsCreateTask
     * @date: 2024/6/19 16:21
     * @param:
     * @return: com.yy.common.core.domain.AjaxResult
     **/
    @GetMapping("/isCreateTask")
    public AjaxResult getIsCreateTask(){
        return success(wsOrderInfoService.getIsCreateTask());
    }

    /**
     * 更新发送时间
     * @param wsOrderInfo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:edit')")
    @Log(title = "WS订单修改发送时间信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSendTime")
    public AjaxResult updateSendTime(@RequestBody WsOrderInfo wsOrderInfo){
        System.out.println("wsOrderInfo = " + wsOrderInfo);
        return success(wsOrderInfoService.updateSendTime(wsOrderInfo));
    }

    /**
     * 更新发送状态
     *
     * @param wsOrderInfo
     * @return
     */
    @PreAuthorize("@ss.hasPermi('manage:wsOrderInfo:edit')")
    @Log(title = "WS订单修改发送时间信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSendStatus")
    public AjaxResult updateSendStatus(@RequestBody WsOrderInfo wsOrderInfo) {
        return success(wsOrderInfoService.updateSendStatus(wsOrderInfo));
    }
}

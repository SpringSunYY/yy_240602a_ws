package com.yy.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.yy.manage.domain.WsUpstreamOrderInfo;
import com.yy.manage.service.IWsUpstreamOrderInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * ws上游订单信息Controller
 * 
 * @author yy
 * @date 2024-06-27
 */
@RestController
@RequestMapping("/manage/wsUpstreamOrderInfo")
public class WsUpstreamOrderInfoController extends BaseController
{
    @Autowired
    private IWsUpstreamOrderInfoService wsUpstreamOrderInfoService;

    /**
     * 查询ws上游订单信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:wsUpstreamOrderInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(WsUpstreamOrderInfo wsUpstreamOrderInfo)
    {
        startPage();
        List<WsUpstreamOrderInfo> list = wsUpstreamOrderInfoService.selectWsUpstreamOrderInfoList(wsUpstreamOrderInfo);
        return getDataTable(list);
    }

    /**
     * 导出ws上游订单信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:wsUpstreamOrderInfo:export')")
    @Log(title = "ws上游订单信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, WsUpstreamOrderInfo wsUpstreamOrderInfo)
    {
        List<WsUpstreamOrderInfo> list = wsUpstreamOrderInfoService.selectWsUpstreamOrderInfoList(wsUpstreamOrderInfo);
        ExcelUtil<WsUpstreamOrderInfo> util = new ExcelUtil<WsUpstreamOrderInfo>(WsUpstreamOrderInfo.class);
        util.exportExcel(response, list, "ws上游订单信息数据");
    }

    /**
     * 获取ws上游订单信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsUpstreamOrderInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(wsUpstreamOrderInfoService.selectWsUpstreamOrderInfoById(id));
    }

    /**
     * 新增ws上游订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsUpstreamOrderInfo:add')")
    @Log(title = "ws上游订单信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WsUpstreamOrderInfo wsUpstreamOrderInfo)
    {
        return toAjax(wsUpstreamOrderInfoService.insertWsUpstreamOrderInfo(wsUpstreamOrderInfo));
    }

    /**
     * 修改ws上游订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsUpstreamOrderInfo:edit')")
    @Log(title = "ws上游订单信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WsUpstreamOrderInfo wsUpstreamOrderInfo)
    {
        return toAjax(wsUpstreamOrderInfoService.updateWsUpstreamOrderInfo(wsUpstreamOrderInfo));
    }

    /**
     * 删除ws上游订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:wsUpstreamOrderInfo:remove')")
    @Log(title = "ws上游订单信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(wsUpstreamOrderInfoService.deleteWsUpstreamOrderInfoByIds(ids));
    }
}

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
import com.yy.manage.domain.IntegralHistoryInfo;
import com.yy.manage.service.IIntegralHistoryInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

import static com.yy.common.constant.DictDataConstants.WS_INTEGRAL_TYPE_0;

/**
 * 积分记录信息Controller
 * 
 * @author yy
 * @date 2024-06-06
 */
@RestController
@RequestMapping("/manage/integralHistoryInfo")
public class IntegralHistoryInfoController extends BaseController
{
    @Autowired
    private IIntegralHistoryInfoService integralHistoryInfoService;

    /**
     * 查询积分记录信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:integralHistoryInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(IntegralHistoryInfo integralHistoryInfo)
    {
        startPage();
        List<IntegralHistoryInfo> list = integralHistoryInfoService.selectIntegralHistoryInfoList(integralHistoryInfo);
        return getDataTable(list);
    }

    /**
     * 导出积分记录信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:integralHistoryInfo:export')")
    @Log(title = "积分记录信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IntegralHistoryInfo integralHistoryInfo)
    {
        List<IntegralHistoryInfo> list = integralHistoryInfoService.selectIntegralHistoryInfoList(integralHistoryInfo);
        ExcelUtil<IntegralHistoryInfo> util = new ExcelUtil<IntegralHistoryInfo>(IntegralHistoryInfo.class);
        util.exportExcel(response, list, "积分记录信息数据");
    }

    /**
     * 获取积分记录信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralHistoryInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(integralHistoryInfoService.selectIntegralHistoryInfoById(id));
    }

    /**
     * 新增积分记录信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralHistoryInfo:add')")
    @Log(title = "积分记录信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IntegralHistoryInfo integralHistoryInfo)
    {
        integralHistoryInfo.setType(WS_INTEGRAL_TYPE_0);
        return toAjax(integralHistoryInfoService.insertIntegralHistoryInfo(integralHistoryInfo));
    }

    /**
     * 修改积分记录信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralHistoryInfo:edit')")
    @Log(title = "积分记录信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IntegralHistoryInfo integralHistoryInfo)
    {
        return toAjax(integralHistoryInfoService.updateIntegralHistoryInfo(integralHistoryInfo));
    }

    /**
     * 删除积分记录信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralHistoryInfo:remove')")
    @Log(title = "积分记录信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(integralHistoryInfoService.deleteIntegralHistoryInfoByIds(ids));
    }
}

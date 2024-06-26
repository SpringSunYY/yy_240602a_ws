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
import com.yy.manage.domain.IntegralInfo;
import com.yy.manage.service.IIntegralInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * 积分信息Controller
 *
 * @author yy
 * @date 2024-06-06
 */
@RestController
@RequestMapping("/manage/integralInfo")
public class IntegralInfoController extends BaseController {
    @Autowired
    private IIntegralInfoService integralInfoService;

    /**
     * 查询积分信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:integralInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(IntegralInfo integralInfo) {
        startPage();
        List<IntegralInfo> list = integralInfoService.selectIntegralInfoList(integralInfo);
        return getDataTable(list);
    }

    /**
     * 导出积分信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:integralInfo:export')")
    @Log(title = "积分信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, IntegralInfo integralInfo) {
        List<IntegralInfo> list = integralInfoService.selectIntegralInfoList(integralInfo);
        ExcelUtil<IntegralInfo> util = new ExcelUtil<IntegralInfo>(IntegralInfo.class);
        util.exportExcel(response, list, "积分信息数据");
    }

    /**
     * 获取积分信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(integralInfoService.selectIntegralInfoById(id));
    }

    /**
     * 新增积分信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralInfo:add')")
    @Log(title = "积分信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody IntegralInfo integralInfo) {
        return toAjax(integralInfoService.insertIntegralInfo(integralInfo));
    }

    /**
     * 修改积分信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralInfo:edit')")
    @Log(title = "积分信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody IntegralInfo integralInfo) {
        return toAjax(integralInfoService.updateIntegralInfo(integralInfo));
    }

    /**
     * 删除积分信息
     */
    @PreAuthorize("@ss.hasPermi('manage:integralInfo:remove')")
    @Log(title = "积分信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(integralInfoService.deleteIntegralInfoByIds(ids));
    }
}

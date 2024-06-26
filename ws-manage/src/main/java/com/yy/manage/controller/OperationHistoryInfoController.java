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
import com.yy.manage.domain.OperationHistoryInfo;
import com.yy.manage.service.IOperationHistoryInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * 操作记录Controller
 * 
 * @author yy
 * @date 2024-06-07
 */
@RestController
@RequestMapping("/manage/operationHistoryInfo")
public class OperationHistoryInfoController extends BaseController
{
    @Autowired
    private IOperationHistoryInfoService operationHistoryInfoService;

    /**
     * 查询操作记录列表
     */
    @PreAuthorize("@ss.hasPermi('manage:operationHistoryInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(OperationHistoryInfo operationHistoryInfo)
    {
        startPage();
        List<OperationHistoryInfo> list = operationHistoryInfoService.selectOperationHistoryInfoList(operationHistoryInfo);
        return getDataTable(list);
    }

    /**
     * 导出操作记录列表
     */
    @PreAuthorize("@ss.hasPermi('manage:operationHistoryInfo:export')")
    @Log(title = "操作记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OperationHistoryInfo operationHistoryInfo)
    {
        List<OperationHistoryInfo> list = operationHistoryInfoService.selectOperationHistoryInfoList(operationHistoryInfo);
        ExcelUtil<OperationHistoryInfo> util = new ExcelUtil<OperationHistoryInfo>(OperationHistoryInfo.class);
        util.exportExcel(response, list, "操作记录数据");
    }

    /**
     * 获取操作记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:operationHistoryInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(operationHistoryInfoService.selectOperationHistoryInfoById(id));
    }

    /**
     * 新增操作记录
     */
    @PreAuthorize("@ss.hasPermi('manage:operationHistoryInfo:add')")
    @Log(title = "操作记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OperationHistoryInfo operationHistoryInfo)
    {
        return toAjax(operationHistoryInfoService.insertOperationHistoryInfo(operationHistoryInfo));
    }

    /**
     * 修改操作记录
     */
    @PreAuthorize("@ss.hasPermi('manage:operationHistoryInfo:edit')")
    @Log(title = "操作记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OperationHistoryInfo operationHistoryInfo)
    {
        return toAjax(operationHistoryInfoService.updateOperationHistoryInfo(operationHistoryInfo));
    }

    /**
     * 删除操作记录
     */
    @PreAuthorize("@ss.hasPermi('manage:operationHistoryInfo:remove')")
    @Log(title = "操作记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(operationHistoryInfoService.deleteOperationHistoryInfoByIds(ids));
    }
}

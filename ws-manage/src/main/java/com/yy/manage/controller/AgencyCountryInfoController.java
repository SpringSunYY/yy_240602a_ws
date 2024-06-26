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
import com.yy.manage.domain.AgencyCountryInfo;
import com.yy.manage.service.IAgencyCountryInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * 代理国家信息Controller
 * 
 * @author yy
 * @date 2024-06-07
 */
@RestController
@RequestMapping("/manage/agencyCountryInfo")
public class AgencyCountryInfoController extends BaseController
{
    @Autowired
    private IAgencyCountryInfoService agencyCountryInfoService;

    /**
     * 查询代理国家信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyCountryInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgencyCountryInfo agencyCountryInfo)
    {
        startPage();
        List<AgencyCountryInfo> list = agencyCountryInfoService.selectAgencyCountryInfoList(agencyCountryInfo);
        return getDataTable(list);
    }

    /**
     * 导出代理国家信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyCountryInfo:export')")
    @Log(title = "代理国家信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgencyCountryInfo agencyCountryInfo)
    {
        List<AgencyCountryInfo> list = agencyCountryInfoService.selectAgencyCountryInfoList(agencyCountryInfo);
        ExcelUtil<AgencyCountryInfo> util = new ExcelUtil<AgencyCountryInfo>(AgencyCountryInfo.class);
        util.exportExcel(response, list, "代理国家信息数据");
    }

    /**
     * 获取代理国家信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyCountryInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(agencyCountryInfoService.selectAgencyCountryInfoById(id));
    }

    /**
     * 新增代理国家信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyCountryInfo:add')")
    @Log(title = "代理国家信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgencyCountryInfo agencyCountryInfo)
    {
        return toAjax(agencyCountryInfoService.batchInsertAgencyCountryInfo(agencyCountryInfo));
    }

    /**
     * 修改代理国家信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyCountryInfo:edit')")
    @Log(title = "代理国家信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgencyCountryInfo agencyCountryInfo)
    {
        return toAjax(agencyCountryInfoService.updateAgencyCountryInfo(agencyCountryInfo));
    }

    /**
     * 删除代理国家信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyCountryInfo:remove')")
    @Log(title = "代理国家信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(agencyCountryInfoService.deleteAgencyCountryInfoByIds(ids));
    }
}

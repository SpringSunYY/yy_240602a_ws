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
import com.yy.manage.domain.CountryInfo;
import com.yy.manage.service.ICountryInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * 国家信息Controller
 * 
 * @author yy
 * @date 2024-06-07
 */
@RestController
@RequestMapping("/manage/countryInfo")
public class CountryInfoController extends BaseController
{
    @Autowired
    private ICountryInfoService countryInfoService;

    /**
     * 查询国家信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:countryInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(CountryInfo countryInfo)
    {
        startPage();
        List<CountryInfo> list = countryInfoService.selectCountryInfoList(countryInfo);
        return getDataTable(list);
    }

    /**
     * 导出国家信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:countryInfo:export')")
    @Log(title = "国家信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CountryInfo countryInfo)
    {
        List<CountryInfo> list = countryInfoService.selectCountryInfoList(countryInfo);
        ExcelUtil<CountryInfo> util = new ExcelUtil<CountryInfo>(CountryInfo.class);
        util.exportExcel(response, list, "国家信息数据");
    }

    /**
     * 获取国家信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:countryInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(countryInfoService.selectCountryInfoById(id));
    }

    /**
     * 新增国家信息
     */
    @PreAuthorize("@ss.hasPermi('manage:countryInfo:add')")
    @Log(title = "国家信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CountryInfo countryInfo)
    {
        return toAjax(countryInfoService.insertCountryInfo(countryInfo));
    }

    /**
     * 修改国家信息
     */
    @PreAuthorize("@ss.hasPermi('manage:countryInfo:edit')")
    @Log(title = "国家信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CountryInfo countryInfo)
    {
        return toAjax(countryInfoService.updateCountryInfo(countryInfo));
    }

    /**
     * 删除国家信息
     */
    @PreAuthorize("@ss.hasPermi('manage:countryInfo:remove')")
    @Log(title = "国家信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(countryInfoService.deleteCountryInfoByIds(ids));
    }
}

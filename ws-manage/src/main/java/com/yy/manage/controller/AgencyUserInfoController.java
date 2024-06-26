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
import com.yy.manage.domain.AgencyUserInfo;
import com.yy.manage.service.IAgencyUserInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * 代理用户信息Controller
 * 
 * @author yy
 * @date 2024-06-07
 */
@RestController
@RequestMapping("/manage/agencyUserInfo")
public class AgencyUserInfoController extends BaseController
{
    @Autowired
    private IAgencyUserInfoService agencyUserInfoService;

    /**
     * 查询代理用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyUserInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(AgencyUserInfo agencyUserInfo)
    {
        startPage();
        List<AgencyUserInfo> list = agencyUserInfoService.selectAgencyUserInfoList(agencyUserInfo);
        return getDataTable(list);
    }

    /**
     * 导出代理用户信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyUserInfo:export')")
    @Log(title = "代理用户信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AgencyUserInfo agencyUserInfo)
    {
        List<AgencyUserInfo> list = agencyUserInfoService.selectAgencyUserInfoList(agencyUserInfo);
        ExcelUtil<AgencyUserInfo> util = new ExcelUtil<AgencyUserInfo>(AgencyUserInfo.class);
        util.exportExcel(response, list, "代理用户信息数据");
    }

    /**
     * 获取代理用户信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyUserInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(agencyUserInfoService.selectAgencyUserInfoById(id));
    }

    /**
     * 新增代理用户信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyUserInfo:add')")
    @Log(title = "代理用户信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AgencyUserInfo agencyUserInfo)
    {
        return toAjax(agencyUserInfoService.batchInsertAgencyUserInfo(agencyUserInfo));
    }

    /**
     * 修改代理用户信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyUserInfo:edit')")
    @Log(title = "代理用户信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AgencyUserInfo agencyUserInfo)
    {
        return toAjax(agencyUserInfoService.updateAgencyUserInfo(agencyUserInfo));
    }

    /**
     * 删除代理用户信息
     */
    @PreAuthorize("@ss.hasPermi('manage:agencyUserInfo:remove')")
    @Log(title = "代理用户信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        return toAjax(agencyUserInfoService.deleteAgencyUserInfoByIds(ids));
    }
}

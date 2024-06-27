package com.yy.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.yy.common.utils.StringUtils;
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
import com.yy.manage.domain.OrderInfo;
import com.yy.manage.service.IOrderInfoService;
import com.yy.common.utils.poi.ExcelUtil;
import com.yy.common.core.page.TableDataInfo;

/**
 * 订单信息Controller
 *
 * @author yy
 * @date 2024-06-07
 */
@RestController
@RequestMapping("/manage/orderInfo")
public class OrderInfoController extends BaseController {
    @Autowired
    private IOrderInfoService orderInfoService;

    /**
     * 查询订单信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(OrderInfo orderInfo) {
        startPage();
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        return getDataTable(list);
    }

    /**
     * 导出订单信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:export')")
    @Log(title = "订单信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, OrderInfo orderInfo) {
        List<OrderInfo> list = orderInfoService.selectOrderInfoList(orderInfo);
        ExcelUtil<OrderInfo> util = new ExcelUtil<OrderInfo>(OrderInfo.class);
        util.exportExcel(response, list, "订单信息数据");
    }

    /**
     * 获取订单信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id) {
        return success(orderInfoService.selectOrderInfoById(id));
    }

    /**
     * 新增订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:add')")
    @Log(title = "订单信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody OrderInfo orderInfo) {
        return toAjax(orderInfoService.insertOrderInfo(orderInfo));
    }

    /**
     * 修改订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:edit')")
    @Log(title = "订单信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody OrderInfo orderInfo) {
        return toAjax(orderInfoService.updateOrderInfo(orderInfo));
    }

    /**
     * 删除订单信息
     */
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:remove')")
    @Log(title = "订单信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids) {
        return toAjax(orderInfoService.deleteOrderInfoByIds(ids));
    }

    @GetMapping("/compute")
    public AjaxResult computeIntegral(OrderInfo orderInfo) {

        return success(orderInfoService.computeIntegral(orderInfo));
    }

    /**
     * @description: 优化订单
     * @author: YY
     * @method: optimize
     * @date: 2024/6/8 16:32
     * @param:
     * @return: com.yy.common.core.domain.AjaxResult
     **/
    @PreAuthorize("@ss.hasPermi('manage:orderInfo:optimize')")
    @GetMapping("/optimize")
    public AjaxResult optimize() {
        //System.out.println(" 优化订单");
        return toAjax(orderInfoService.optimize());
    }
}

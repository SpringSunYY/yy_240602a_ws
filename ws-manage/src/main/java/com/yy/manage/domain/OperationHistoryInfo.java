package com.yy.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * 操作记录对象 tb_operation_history_info
 *
 * @author yy
 * @date 2024-06-09
 */
public class OperationHistoryInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 主键 */
    @Excel(name = "编号 主键")
    private String id;

    /** 申请编号 */
    @Excel(name = "申请编号")
    private String orderId;
    private String orderName;

    /** 申请人 */
    @Excel(name = "申请人")
    private Long userId;

    /** 部门 */
    @Excel(name = "部门")
    private Long deptId;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderId()
    {
        return orderId;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("orderId", getOrderId())
                .append("remark", getRemark())
                .append("userId", getUserId())
                .append("createBy", getCreateBy())
                .append("deptId", getDeptId())
                .append("createTime", getCreateTime())
                .toString();
    }
}

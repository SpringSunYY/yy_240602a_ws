package com.yy.manage.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * 积分记录信息对象 tb_integral_history_info
 *
 * @author yy
 * @date 2024-06-06
 */
public class IntegralHistoryInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 积分 */
    @Excel(name = "积分")
    private BigDecimal integral;

    /** 金额 */
    @Excel(name = "金额")
    private BigDecimal money;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 消费前积分 */
    @Excel(name = "消费前积分")
    private BigDecimal startIntegral;

    /** 消费后积分 */
    @Excel(name = "消费后积分")
    private BigDecimal endIntegral;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 用户 */
    @Excel(name = "用户")
    private Long userId;

    /** 部门 */
    @Excel(name = "部门")
    private Long deptId;

    /** 删除 */
    @Excel(name = "删除")
    private String isDelete;

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
    public void setIntegral(BigDecimal integral)
    {
        this.integral = integral;
    }

    public BigDecimal getIntegral()
    {
        return integral;
    }
    public void setMoney(BigDecimal money)
    {
        this.money = money;
    }

    public BigDecimal getMoney()
    {
        return money;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }
    public void setStartIntegral(BigDecimal startIntegral)
    {
        this.startIntegral = startIntegral;
    }

    public BigDecimal getStartIntegral()
    {
        return startIntegral;
    }
    public void setEndIntegral(BigDecimal endIntegral)
    {
        this.endIntegral = endIntegral;
    }

    public BigDecimal getEndIntegral()
    {
        return endIntegral;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
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
    public void setIsDelete(String isDelete)
    {
        this.isDelete = isDelete;
    }

    public String getIsDelete()
    {
        return isDelete;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("integral", getIntegral())
                .append("money", getMoney())
                .append("type", getType())
                .append("startIntegral", getStartIntegral())
                .append("endIntegral", getEndIntegral())
                .append("status", getStatus())
                .append("userId", getUserId())
                .append("deptId", getDeptId())
                .append("remark", getRemark())
                .append("createBy", getCreateBy())
                .append("isDelete", getIsDelete())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}

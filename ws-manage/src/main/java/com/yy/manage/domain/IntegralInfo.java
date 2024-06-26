package com.yy.manage.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * 积分信息对象 tb_integral_info
 * 
 * @author yy
 * @date 2024-06-06
 */
public class IntegralInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 积分 */
    @Excel(name = "积分")
    private BigDecimal integral;

    /** 冻结积分 */
    @Excel(name = "冻结积分")
    private BigDecimal freezeIntegral;

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
    public void setFreezeIntegral(BigDecimal freezeIntegral) 
    {
        this.freezeIntegral = freezeIntegral;
    }

    public BigDecimal getFreezeIntegral() 
    {
        return freezeIntegral;
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
            .append("freezeIntegral", getFreezeIntegral())
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

package com.yy.manage.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * 代理国家信息对象 tb_agency_country_info
 * 
 * @author yy
 * @date 2024-06-07
 */
public class AgencyCountryInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 国家 */
    @Excel(name = "国家")
    private String countryId;
    private String[] country;
    private String countryName;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal prices;

    /** 比例 */
    @Excel(name = "比例")
    private Float proportion;

    /** 部门 */
    @Excel(name = "部门")
    private Long deptId;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String[] getCountry() {
        return country;
    }

    public void setCountry(String[] country) {
        this.country = country;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setCountryId(String countryId) 
    {
        this.countryId = countryId;
    }

    public String getCountryId() 
    {
        return countryId;
    }
    public void setPrices(BigDecimal prices) 
    {
        this.prices = prices;
    }

    public BigDecimal getPrices() 
    {
        return prices;
    }
    public void setProportion(Float proportion)
    {
        this.proportion = proportion;
    }

    public Float getProportion()
    {
        return proportion;
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
            .append("countryId", getCountryId())
            .append("prices", getPrices())
            .append("proportion", getProportion())
            .append("remark", getRemark())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

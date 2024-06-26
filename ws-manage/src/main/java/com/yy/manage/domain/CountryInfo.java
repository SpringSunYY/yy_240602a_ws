package com.yy.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * 国家信息对象 tb_country_info
 * 
 * @author yy
 * @date 2024-06-07
 */
public class CountryInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 国家或地区 */
    @Excel(name = "国家或地区")
    private String name;

    /** 名称缩写 */
    @Excel(name = "名称缩写")
    private String adName;

    /** 电话代码 */
    @Excel(name = "电话代码")
    private String phoneCode;

    /** 全称 */
    @Excel(name = "全称")
    private String allName;


    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setAdName(String adName) 
    {
        this.adName = adName;
    }

    public String getAdName() 
    {
        return adName;
    }
    public void setPhoneCode(String phoneCode) 
    {
        this.phoneCode = phoneCode;
    }

    public String getPhoneCode() 
    {
        return phoneCode;
    }
    public void setAllName(String allName) 
    {
        this.allName = allName;
    }

    public String getAllName() 
    {
        return allName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("adName", getAdName())
            .append("phoneCode", getPhoneCode())
            .append("allName", getAllName())
            .append("createBy", getCreateBy())
            .append("remark", getRemark())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

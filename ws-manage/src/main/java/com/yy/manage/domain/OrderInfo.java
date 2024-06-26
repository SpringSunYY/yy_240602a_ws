package com.yy.manage.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * 订单信息对象 tb_order_info
 * 
 * @author yy
 * @date 2024-06-07
 */
public class OrderInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 名称 */
    @Excel(name = "名称")
    private String name;

    /** 类型 */
    @Excel(name = "类型")
    private String type;

    /** 发送类型 */
    @Excel(name = "发送类型")
    private String sendType;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal prices;

    /** 国家 */
    @Excel(name = "国家")
    private String countryId;
    private String countryName;

    /** 数量 */
    @Excel(name = "数量")
    private Long orderNumber;

    /** 实际数量 */
    @Excel(name = "实际数量")
    private Long actualNumber;

    /** 优化数量 */
    @Excel(name = "优化数量")
    private Long optimizedNumber;

    /** 完成数量 */
    @Excel(name = "完成数量")
    private Long accomplishNumber;

    /** 使用积分 */
    @Excel(name = "使用积分")
    private BigDecimal useIntegral;

    /** 实际积分 */
    @Excel(name = "实际积分")
    private BigDecimal actualIntegral;

    /** 文件 */
    @Excel(name = "文件")
    private String fileCotent;

    /** 过滤文件 */
    @Excel(name = "过滤文件")
    private String fileFilter;

    /** 文案内容 */
    @Excel(name = "文案内容")
    private String copyContent;

    /** 链接内容 */
    @Excel(name = "链接内容")
    private String linkContent;

    /** 按钮 */
    @Excel(name = "按钮")
    private String buttons;

    /** 链接 */
    @Excel(name = "链接")
    private String links;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

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
    private String optimizeUser;
    private Long optimizeUserId;

    public Long getOptimizeUserId() {
        return optimizeUserId;
    }

    public void setOptimizeUserId(Long optimizeUserId) {
        this.optimizeUserId = optimizeUserId;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getOptimizeUser() {
        return optimizeUser;
    }

    public void setOptimizeUser(String optimizeUser) {
        this.optimizeUser = optimizeUser;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

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
    public void setType(String type) 
    {
        this.type = type;
    }

    public String getType() 
    {
        return type;
    }
    public void setSendType(String sendType) 
    {
        this.sendType = sendType;
    }

    public String getSendType() 
    {
        return sendType;
    }
    public void setPrices(BigDecimal prices) 
    {
        this.prices = prices;
    }

    public BigDecimal getPrices() 
    {
        return prices;
    }
    public void setCountryId(String countryId) 
    {
        this.countryId = countryId;
    }

    public String getCountryId() 
    {
        return countryId;
    }
    public void setOrderNumber(Long orderNumber) 
    {
        this.orderNumber = orderNumber;
    }

    public Long getOrderNumber() 
    {
        return orderNumber;
    }
    public void setActualNumber(Long actualNumber) 
    {
        this.actualNumber = actualNumber;
    }

    public Long getActualNumber()
    {
        return actualNumber;
    }
    public void setOptimizedNumber(Long optimizedNumber) 
    {
        this.optimizedNumber = optimizedNumber;
    }

    public Long getOptimizedNumber() 
    {
        return optimizedNumber;
    }
    public void setAccomplishNumber(Long accomplishNumber) 
    {
        this.accomplishNumber = accomplishNumber;
    }

    public Long getAccomplishNumber() 
    {
        return accomplishNumber;
    }
    public void setUseIntegral(BigDecimal useIntegral) 
    {
        this.useIntegral = useIntegral;
    }

    public BigDecimal getUseIntegral() 
    {
        return useIntegral;
    }
    public void setActualIntegral(BigDecimal actualIntegral) 
    {
        this.actualIntegral = actualIntegral;
    }

    public BigDecimal getActualIntegral() 
    {
        return actualIntegral;
    }
    public void setFileCotent(String fileCotent) 
    {
        this.fileCotent = fileCotent;
    }

    public String getFileCotent() 
    {
        return fileCotent;
    }
    public void setFileFilter(String fileFilter) 
    {
        this.fileFilter = fileFilter;
    }

    public String getFileFilter() 
    {
        return fileFilter;
    }
    public void setCopyContent(String copyContent) 
    {
        this.copyContent = copyContent;
    }

    public String getCopyContent() 
    {
        return copyContent;
    }
    public void setLinkContent(String linkContent) 
    {
        this.linkContent = linkContent;
    }

    public String getLinkContent() 
    {
        return linkContent;
    }
    public void setButtons(String buttons) 
    {
        this.buttons = buttons;
    }

    public String getButtons() 
    {
        return buttons;
    }
    public void setLinks(String links) 
    {
        this.links = links;
    }

    public String getLinks() 
    {
        return links;
    }
    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
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
            .append("name", getName())
            .append("type", getType())
            .append("sendType", getSendType())
            .append("prices", getPrices())
            .append("countryId", getCountryId())
            .append("orderNumber", getOrderNumber())
            .append("actualNumber", getActualNumber())
            .append("optimizedNumber", getOptimizedNumber())
            .append("accomplishNumber", getAccomplishNumber())
            .append("useIntegral", getUseIntegral())
            .append("actualIntegral", getActualIntegral())
            .append("fileCotent", getFileCotent())
            .append("fileFilter", getFileFilter())
            .append("copyContent", getCopyContent())
            .append("linkContent", getLinkContent())
            .append("buttons", getButtons())
            .append("links", getLinks())
            .append("sendTime", getSendTime())
            .append("endTime", getEndTime())
            .append("status", getStatus())
            .append("userId", getUserId())
            .append("deptId", getDeptId())
            .append("remark", getRemark())
            .append("isDelete", getIsDelete())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

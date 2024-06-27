package com.yy.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * ws上游订单信息对象 tb_ws_upstream_order_info
 * 
 * @author yy
 * @date 2024-06-27
 */
public class WsUpstreamOrderInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 订单名称 */
    @Excel(name = "订单名称")
    private String name;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String taskId;

    /** 发送类型 */
    @Excel(name = "发送类型")
    private String sendType;

    /** 使用 */
    @Excel(name = "使用")
    private String isUse;

    /** 使用订单 */
    @Excel(name = "使用订单")
    private String useOrderId;

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
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setSendType(String sendType) 
    {
        this.sendType = sendType;
    }

    public String getSendType() 
    {
        return sendType;
    }
    public void setIsUse(String isUse) 
    {
        this.isUse = isUse;
    }

    public String getIsUse() 
    {
        return isUse;
    }
    public void setUseOrderId(String useOrderId) 
    {
        this.useOrderId = useOrderId;
    }

    public String getUseOrderId() 
    {
        return useOrderId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("taskId", getTaskId())
            .append("sendType", getSendType())
            .append("isUse", getIsUse())
            .append("useOrderId", getUseOrderId())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

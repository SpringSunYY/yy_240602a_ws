package com.yy.manage.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.yy.common.annotation.Excel;
import com.yy.common.core.domain.BaseEntity;

/**
 * WS订单信息对象 tb_ws_order_info
 * 
 * @author yy
 * @date 2024-06-13
 */
public class WsOrderInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @Excel(name = "编号")
    private String id;

    /** 订单编号 */
    @Excel(name = "订单编号")
    private String taskId;

    /** 订单名称 */
    @Excel(name = "订单名称")
    private String name;

    /** 国家 */
    @Excel(name = "国家")
    private String countryId;
    private String countryName;

    /** 发送类型 */
    @Excel(name = "发送类型")
    private String sendType;

    /** 价格 */
    @Excel(name = "价格")
    private BigDecimal prices;

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
    private String fileContent;

    /** 过滤文件 */
    @Excel(name = "过滤文件")
    private String fileFilter;


    /** 文案内容 */
    @Excel(name = "文案内容")
    private String copyContent;

    /** 链接内容 */
    @Excel(name = "链接内容")
    private String linkContent;

    /** 客服号码 */
    @Excel(name = "客服号码")
    private String servicePhone;

    /** 按钮1 */
    @Excel(name = "按钮1")
    private String buttons1;

    /** 链接1 */
    @Excel(name = "链接1")
    private String links1;

    /** 按钮2 */
    @Excel(name = "按钮2")
    private String buttons2;

    /** 链接2 */
    @Excel(name = "链接2")
    private String links2;

    /** 按钮3 */
    @Excel(name = "按钮3")
    private String buttons3;

    /** 链接3 */
    @Excel(name = "链接3")
    private String links3;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date sendTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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

    /** 隐藏文件 */
    @Excel(name = "隐藏文件")
    private String discardedFile;

    /** 返回文件 */
    @Excel(name = "返回文件")
    private String resSuccFile;

    private boolean isJudge=true;

    public boolean isJudge() {
        return isJudge;
    }

    public void setJudge(boolean judge) {
        isJudge = judge;
    }

    private String outPutFilePath;

    public String getResSuccFile() {
        return resSuccFile;
    }

    public void setResSuccFile(String resSuccFile) {
        this.resSuccFile = resSuccFile;
    }

    public String getDiscardedFile() {
        return discardedFile;
    }

    public void setDiscardedFile(String discardedFile) {
        this.discardedFile = discardedFile;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getOutPutFilePath() {
        return outPutFilePath;
    }

    public void setOutPutFilePath(String outPutFilePath) {
        this.outPutFilePath = outPutFilePath;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setTaskId(String taskId) 
    {
        this.taskId = taskId;
    }

    public String getTaskId() 
    {
        return taskId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCountryId(String countryId) 
    {
        this.countryId = countryId;
    }

    public String getCountryId() 
    {
        return countryId;
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
    public void setFileContent(String fileContent) 
    {
        this.fileContent = fileContent;
    }

    public String getFileContent() 
    {
        return fileContent;
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
    public void setServicePhone(String servicePhone) 
    {
        this.servicePhone = servicePhone;
    }

    public String getServicePhone() 
    {
        return servicePhone;
    }
    public void setButtons1(String buttons1) 
    {
        this.buttons1 = buttons1;
    }

    public String getButtons1() 
    {
        return buttons1;
    }
    public void setLinks1(String links1) 
    {
        this.links1 = links1;
    }

    public String getLinks1() 
    {
        return links1;
    }
    public void setButtons2(String buttons2) 
    {
        this.buttons2 = buttons2;
    }

    public String getButtons2() 
    {
        return buttons2;
    }
    public void setLinks2(String links2) 
    {
        this.links2 = links2;
    }

    public String getLinks2() 
    {
        return links2;
    }
    public void setButtons3(String buttons3) 
    {
        this.buttons3 = buttons3;
    }

    public String getButtons3() 
    {
        return buttons3;
    }
    public void setLinks3(String links3) 
    {
        this.links3 = links3;
    }

    public String getLinks3() 
    {
        return links3;
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
        return "WsOrderInfo{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", name='" + name + '\'' +
                ", countryId='" + countryId + '\'' +
                ", sendType='" + sendType + '\'' +
                ", prices=" + prices +
                ", orderNumber=" + orderNumber +
                ", actualNumber=" + actualNumber +
                ", optimizedNumber=" + optimizedNumber +
                ", accomplishNumber=" + accomplishNumber +
                ", useIntegral=" + useIntegral +
                ", actualIntegral=" + actualIntegral +
                ", fileContent='" + fileContent + '\'' +
                ", fileFilter='" + fileFilter + '\'' +
                ", copyContent='" + copyContent + '\'' +
                ", linkContent='" + linkContent + '\'' +
                ", servicePhone='" + servicePhone + '\'' +
                ", buttons1='" + buttons1 + '\'' +
                ", links1='" + links1 + '\'' +
                ", buttons2='" + buttons2 + '\'' +
                ", links2='" + links2 + '\'' +
                ", buttons3='" + buttons3 + '\'' +
                ", links3='" + links3 + '\'' +
                ", sendTime=" + sendTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", userId=" + userId +
                ", deptId=" + deptId +
                ", isDelete='" + isDelete + '\'' +
                ", outPutFilePath='" + outPutFilePath + '\'' +
                '}';
    }
}

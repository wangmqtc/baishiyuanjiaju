package com.baishiyuan.domain;

import org.dozer.Mapping;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户账户表
 */
@Document(collection = "order")
public class Order implements Serializable{

    private static final long serialVersionUID = -407170992132682561L;
    @Id
    private String id;

    /**订单序列号*/
    private String orderserializable;

    /**用户ID*/
    private Integer userId;

    /**地址*/
    @Mapping("address")
    private String address;

    /**收件人电话*/
    @Mapping("phone")
    private String mbn;

    /**收件人名称*/
    @Mapping("name")
    private String clientName;

    /**收件人座机*/
    private String clientPhone;

    /**收件人邮编*/
    private String zcode;

    /**订购的物品ID*/
    private List<OrderGoods> orderGoodss;

    /**花费的价钱，以分为单位*/
    private Integer costMoney;

    /** 是否有折扣,0是没折扣，1是有折扣*/
    private Integer isDisCount;

    /**物流公司名称*/
    private String companyName;

    /**物流费用*/
    private String logisticsCost;

    /**物流单号*/
    private String logisticsNumber;

    /**0为否,1为客户取消，2为客服取消*/
    private Integer isCancel;

    /**状态 0是未处理，1是备货中，2是已发货，3是已取消*/
    private Integer status;

    /**取消原因*/
    private String cancelReason;

    /**是否结单付款 0是未结单付款，1是结单付款*/
    private Integer isCost;

    /** 创建时间*/
    private Date gmtCreate;

    /** 创建时间*/
    private Integer createDay;

    /** 更新时间*/
    private Date gmtModified;

    /** 创建人*/
    private Integer creator;

    /** 修改人*/
    private Integer modifier;

    /** 接受此订单的接单员ID*/
    private Integer reciver;

    /** 打印次数*/
    private Integer printNumber;

    /** 订单备注*/
    @Mapping("remark")
    private String remark;

    /** 发送时间*/
    private Date sendTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMbn() {
        return mbn;
    }

    public void setMbn(String mbn) {
        this.mbn = mbn;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public List<OrderGoods> getOrderGoodss() {
        return orderGoodss;
    }

    public void setOrderGoodss(List<OrderGoods> orderGoodss) {
        this.orderGoodss = orderGoodss;
    }

    public Integer getCostMoney() {
        return costMoney;
    }

    public void setCostMoney(Integer costMoney) {
        this.costMoney = costMoney;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLogisticsCost() {
        return logisticsCost;
    }

    public void setLogisticsCost(String logisticsCost) {
        this.logisticsCost = logisticsCost;
    }

    public Integer getIsCancel() {
        return isCancel;
    }

    public void setIsCancel(Integer isCancel) {
        this.isCancel = isCancel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public Integer getIsCost() {
        return isCost;
    }

    public void setIsCost(Integer isCost) {
        this.isCost = isCost;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Integer getModifier() {
        return modifier;
    }

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public Integer getReciver() {
        return reciver;
    }

    public void setReciver(Integer reciver) {
        this.reciver = reciver;
    }

    public Integer getCreateDay() {
        return createDay;
    }

    public void setCreateDay(Integer createDay) {
        this.createDay = createDay;
    }

    public Integer getIsDisCount() {
        return isDisCount;
    }

    public void setIsDisCount(Integer isDisCount) {
        this.isDisCount = isDisCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getZcode() {
        return zcode;
    }

    public void setZcode(String zcode) {
        this.zcode = zcode;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public String getOrderserializable() {
        return orderserializable;
    }

    public void setOrderserializable(String orderserializable) {
        this.orderserializable = orderserializable;
    }

    public Integer getPrintNumber() {
        return printNumber;
    }

    public void setPrintNumber(Integer printNumber) {
        this.printNumber = printNumber;
    }
}


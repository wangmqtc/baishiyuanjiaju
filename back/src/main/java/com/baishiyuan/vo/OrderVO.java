package com.baishiyuan.vo;

import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单
 */
public class OrderVO implements Serializable{

    private static final long serialVersionUID = 1154720264719048125L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**是否删除*/
    private Integer  isDeleted;

    /**地址*/
    private String address;

    /**收件人电话*/
    private String mbn;

    /**收件人电话*/
    private String clientName;

    /**订购的物品ID*/
    private List<String> productIds;

    /**订购的物品的名称*/
    private List<String> productNames;

    /**花费的价钱，以分为单位*/
    private Double costMoney;

    /** 是否加急,0是不加急，1是加急*/
    private Integer isUrgent;

    /** 是否有折扣,0是没折扣，1是有折扣*/
    private Integer isDisCount;

    /**物流公司名称*/
    private String companyName;

    /**物流费用*/
    private Double logisticsCost;

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

    /**头像*/
    private String logoId;

    /**用户手机号*/
    private String creatorMbn;

    /**用户昵称*/
    private String nickName;

    /**真实姓名*/
    private String realName;

    /** 订单备注*/
    private String remark;

    /** 字符串时间*/
    private String gmtCreateStr;

    /**收件人座机*/
    private String clientPhone;

    /**收件人邮编*/
    private String zcode;

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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
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

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }

    public Double getCostMoney() {
        return costMoney;
    }

    public void setCostMoney(Double costMoney) {
        this.costMoney = costMoney;
    }

    public Integer getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Integer isUrgent) {
        this.isUrgent = isUrgent;
    }

    public Integer getIsDisCount() {
        return isDisCount;
    }

    public void setIsDisCount(Integer isDisCount) {
        this.isDisCount = isDisCount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getLogisticsCost() {
        return logisticsCost;
    }

    public void setLogisticsCost(Double logisticsCost) {
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

    public Integer getCreateDay() {
        return createDay;
    }

    public void setCreateDay(Integer createDay) {
        this.createDay = createDay;
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

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    public String getCreatorMbn() {
        return creatorMbn;
    }

    public void setCreatorMbn(String creatorMbn) {
        this.creatorMbn = creatorMbn;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGmtCreateStr() {
        return gmtCreateStr;
    }

    public void setGmtCreateStr(String gmtCreateStr) {
        this.gmtCreateStr = gmtCreateStr;
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
}

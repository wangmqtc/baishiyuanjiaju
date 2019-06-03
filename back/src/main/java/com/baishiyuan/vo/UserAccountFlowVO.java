package com.baishiyuan.vo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户表
 */
public class UserAccountFlowVO implements Serializable{

    private static final long serialVersionUID = -1027436652364936384L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**用户昵称*/
    private String nickName;

    /**用户头像*/
    private String logoId;

    /**是否删除*/
    private Integer  isDeleted;

    /**操作,0是充值钱，1是减钱*/
    private Integer operation;

    /**操作前的金额*/
    private Double prevMoney;

    /** 操作后的金额*/
    private Double currentMoney;

    /** 变化的金额*/
    private Double changeMoney;

    /** 变化的原因*/
    private String reason;

    /** 创建时间*/
    private Date gmtCreate;

    /** 创建人*/
    private int creator;

    /** 创建日期*/
    private Integer creaDay;

    /** 操作金额时间类型 0是订单，1是取消订单，2是充值*/
    private Integer changeReasonType;

    /** 相应的事件ID*/
    private String eventId;

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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Double getPrevMoney() {
        return prevMoney;
    }

    public void setPrevMoney(Double prevMoney) {
        this.prevMoney = prevMoney;
    }

    public Double getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(Double currentMoney) {
        this.currentMoney = currentMoney;
    }

    public Double getChangeMoney() {
        return changeMoney;
    }

    public void setChangeMoney(Double changeMoney) {
        this.changeMoney = changeMoney;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public Integer getCreaDay() {
        return creaDay;
    }

    public void setCreaDay(Integer creaDay) {
        this.creaDay = creaDay;
    }

    public Integer getChangeReasonType() {
        return changeReasonType;
    }

    public void setChangeReasonType(Integer changeReasonType) {
        this.changeReasonType = changeReasonType;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

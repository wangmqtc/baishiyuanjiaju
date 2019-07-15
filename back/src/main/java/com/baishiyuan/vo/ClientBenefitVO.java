package com.baishiyuan.vo;

import java.io.Serializable;

/**
 * 用户优惠表
 */
public class ClientBenefitVO implements Serializable{

    private static final long serialVersionUID = -5602097333507502986L;

    /**用户ID*/
    private Integer userId;

    /**是否删除*/
    private Integer  isDeleted;

    /**优惠类型,0是折扣，1是加急*/
    private Integer type;

    /**优惠类型,0是没有设置，1是有设置此优惠*/
    private Integer status;

    /**折扣*/
    private Double disCount;

    /** 每天最大的加急单数*/
    private Integer urgentCount;

    /**用户昵称*/
    private String nickName;

    /**真实姓名*/
    private String realName;

    /**用户手机号*/
    private String mbn;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Double getDisCount() {
        return disCount;
    }

    public void setDisCount(Double disCount) {
        this.disCount = disCount;
    }

    public Integer getUrgentCount() {
        return urgentCount;
    }

    public void setUrgentCount(Integer urgentCount) {
        this.urgentCount = urgentCount;
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

    public String getMbn() {
        return mbn;
    }

    public void setMbn(String mbn) {
        this.mbn = mbn;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

package com.baishiyuan.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/7/25 0025.
 */
public class FlowVO implements Serializable{
    private static final long serialVersionUID = 7628543957605246340L;

    /**用户ID*/
    private Integer userId;

    /** 变化的金额*/
    private Double changeMoney;

    /** 变化的原因*/
    private String reason;

    /** 创建时间*/
    private String gmtCreate;

    private String clientName;

    private String nickName;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}

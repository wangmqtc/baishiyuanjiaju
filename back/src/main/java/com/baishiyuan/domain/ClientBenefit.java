package com.baishiyuan.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户表
 */
@Document(collection = "client_benefit")
public class ClientBenefit implements Serializable{

    private static final long serialVersionUID = -5130220587896944783L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**优惠类型,0是折扣，1是加急*/
    private Integer type;

    /**折扣*/
    private Double disCount;

    /** 每天最大的加急单数*/
    private Integer urgentCount;

    /** 创建时间*/
    private Date gmtCreate;

    /** 更新时间*/
    private Date gmtModified;

    /** 创建人*/
    private Integer creator;

    /** 修改人*/
    private Integer modifier;

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
}

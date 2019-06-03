package com.baishiyuan.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户表
 */
@Document(collection = "user_account")
public class UserAccount implements Serializable{

    private static final long serialVersionUID = -1740334829909265466L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**是否删除*/
    private Integer  isDeleted;

    /**总资产*/
    private Integer totalAssets;

    /**可用资产*/
    private Integer availableAssets;

    /** 冻结资产*/
    private Integer frozenAssets;

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

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(Integer totalAssets) {
        this.totalAssets = totalAssets;
    }

    public Integer getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(Integer availableAssets) {
        this.availableAssets = availableAssets;
    }

    public Integer getFrozenAssets() {
        return frozenAssets;
    }

    public void setFrozenAssets(Integer frozenAssets) {
        this.frozenAssets = frozenAssets;
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

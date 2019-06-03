package com.baishiyuan.vo;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户表
 */
public class UserAccountVO implements Serializable{

    private static final long serialVersionUID = 5975990047666001424L;
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

    /**总资产*/
    private Double totalAssets;

    /**可用资产*/
    private Double availableAssets;

    /** 冻结资产*/
    private Double frozenAssets;

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

    public Double getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(Double totalAssets) {
        this.totalAssets = totalAssets;
    }

    public Double getAvailableAssets() {
        return availableAssets;
    }

    public void setAvailableAssets(Double availableAssets) {
        this.availableAssets = availableAssets;
    }

    public Double getFrozenAssets() {
        return frozenAssets;
    }

    public void setFrozenAssets(Double frozenAssets) {
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
}

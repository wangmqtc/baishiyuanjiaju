package com.baishiyuan.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 */
@Document(collection = "user_info")
public class UserInfo implements Serializable{
    private static final long serialVersionUID = -7773413102189848218L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**是否删除*/
    private Integer  isDeleted;

    /**用户昵称*/
    private String nickName;

    /**真实姓名*/
    private String realName;

    /** 创建时间*/
    private Date gmtCreate;

    /** 更新时间*/
    private Date gmtModified;

    /**头像*/
    private String logoId;

    /**用户手机号*/
    private String mbn;

    /**加密后的密码*/
    private String passwd;

    /**类型:0超级管理员, 1是接单员, 2是普通用户,3是子超级管理员*/
    private Integer type;

    /**是否冻结，0是未冻结，1是冻结*/
    private Integer isFrozen;

    /**如果是子超级管理员，权限, 7种权限，7位二进制表示 0000000 从低到高
     * 密码重置、修改帐号资料、冻结（冻结后无法使用）
     * 客户权限设置
     * 产品管理
     * 财务管理
     * 查看财务记录
     * 订单管理
     * 账号设置 针对用户自己*/
    private Integer authority;

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

    public String getLogoId() {
        return logoId;
    }

    public void setLogoId(String logoId) {
        this.logoId = logoId;
    }

    public String getMbn() {
        return mbn;
    }

    public void setMbn(String mbn) {
        this.mbn = mbn;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsFrozen() {
        return isFrozen;
    }

    public void setIsFrozen(Integer isFrozen) {
        this.isFrozen = isFrozen;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId=" + userId +
                ", isDeleted=" + isDeleted +
                ", nickName='" + nickName + '\'' +
                ", realName='" + realName + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", logoId='" + logoId + '\'' +
                ", mbn='" + mbn + '\'' +
                ", passwd='" + passwd + '\'' +
                ", type=" + type +
                ", isFrozen=" + isFrozen +
                '}';
    }

    public Integer getAuthority() {
        return authority;
    }

    public void setAuthority(Integer authority) {
        this.authority = authority;
    }
}

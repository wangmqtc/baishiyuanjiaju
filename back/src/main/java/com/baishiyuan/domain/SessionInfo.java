package com.baishiyuan.domain;

import java.io.Serializable;

/**
 * session对象
 */

public class SessionInfo implements Serializable{

    private static final long serialVersionUID = 150811738417387291L;

    /**用户ID*/
    private Integer userId;

    /**用户昵称*/
    private String nickName;

    /**用户手机号*/
    private String mbn;

    /**类型:0超级管理员, 1是接单员, 2是普通用户,3是子超级管理员*/
    private Integer type;

    /**是否冻结，0是未冻结，1是冻结*/
    private Integer isFrozen;

    /**如果是子超级管理员，权限, 5种权限，5位二进制表示 00000 从低到高
     * 密码重置、修改帐号资料、冻结（冻结后无法使用）
     * 客户权限设置
     * 产品管理
     * 财务管理
     * 查看财务记录 */
    private Integer authority;

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

    public String getMbn() {
        return mbn;
    }

    public void setMbn(String mbn) {
        this.mbn = mbn;
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
        return "SessionInfo{" +
                "userId=" + userId +
                ", nickName='" + nickName + '\'' +
                ", mbn='" + mbn + '\'' +
                ", type=" + type +
                ", isFrozen=" + isFrozen +
                ", authority=" + authority +
                '}';
    }

    public Integer getAuthority() {
        return authority;
    }

    public void setAuthority(Integer authority) {
        this.authority = authority;
    }
}

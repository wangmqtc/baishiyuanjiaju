package com.baishiyuan.DTO;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class OrderDTO implements Serializable{
    private static final long serialVersionUID = 7366758775353194140L;
    @NotEmpty(message="用户地址不能为空")
    private String address;

    /**产品名称*/
    @NotEmpty(message="用户电话不能为空")
    private String phone;

    /**图片地址*/
    @NotEmpty(message="用户姓名不能为空")
    private String name;

    /**用户备注*/
    private String remark;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

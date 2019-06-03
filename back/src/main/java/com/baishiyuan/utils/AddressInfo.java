package com.baishiyuan.utils;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
public class AddressInfo implements Serializable{

    private static final long serialVersionUID = 5621085082333404238L;
    private String address;

    private String phone;

    private String name;

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
}

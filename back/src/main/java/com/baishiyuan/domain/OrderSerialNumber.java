package com.baishiyuan.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户账户表
 */
@Document(collection = "order_serialNumber")
public class OrderSerialNumber implements Serializable{

    private static final long serialVersionUID = -850002680466204291L;
    @Id
    private String id;

    /**日期*/
    private Integer day;

    /**增长的序号*/
    private Integer serialNumber;

    /**日期*/
    private Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}


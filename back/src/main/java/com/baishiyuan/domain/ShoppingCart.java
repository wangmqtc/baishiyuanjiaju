package com.baishiyuan.domain;

import org.dozer.Mapping;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 货物表
 */
@Document(collection = "shopping_cart")
public class ShoppingCart implements Serializable{

    private static final long serialVersionUID = -8950403375086719909L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**当前物品的购买数量*/
    @Mapping("number")
    private Integer number;

    /** 商品ID*/
    @Mapping("goodsId")
    private String goodsId;

    /** 创建时间*/
    private Date gmtCreate;

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


    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}

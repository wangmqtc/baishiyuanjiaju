package com.baishiyuan.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@Document(collection = "goods_model")
public class GoodsModel implements Serializable {

    private static final long serialVersionUID = 6178519730758256437L;
    @Id
    private String id;
    private String model;
    private int number;
    private String description;
    private java.util.Date gmtCreate;
    private java.util.Date gmtModified;
    private Integer creator;
    private Integer modifier;
    
    private String goodsIdRandom;

    private String publishName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getGoodsIdRandom() {
        return goodsIdRandom;
    }

    public void setGoodsIdRandom(String goodsIdRandom) {
        this.goodsIdRandom = goodsIdRandom;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }
}

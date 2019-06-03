package com.baishiyuan.vo;

import org.dozer.Mapping;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * 货物表
 */
public class ShoppingCartVO implements Serializable{

    private static final long serialVersionUID = -8950403375086719909L;
    @Id
    private String id;

    /**用户ID*/
    private Integer userId;

    /**当前物品的购买数量*/
    private Integer number;

    /** 商品ID*/
    @Mapping("id")
    private String goodsId;

    /** 创建时间*/
    private Date gmtCreate;

    /**图片地址*/
    @Mapping("image")
    private String image;

    /**产品颜色*/
    @Mapping("color")
    private String color;

    /**该产品材质*/
    @Mapping("material")
    private String material;

    /**产品价格*/
    @Mapping("price")
    private Integer price;

    /**描述*/
    @Mapping("description")
    private String description;

    /**产品类型*/
    @Mapping("model")
    private String model;

    /**型号ID*/
    @Mapping("modelId")
    private String modelId;

    /**产品名称*/
    @Mapping("name")
    private String name;

    @Mapping("publishName")
    private String publishName;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }
}

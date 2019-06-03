package com.baishiyuan.vo;

import org.dozer.Mapping;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;


public class GoodsVO implements Serializable{

    private static final long serialVersionUID = 1712150214635018141L;
    @Id
    @Mapping("id")
    private String id;

    /**产品名称*/
    private String name;

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

    /** 创建时间*/
    @Mapping("gmtCreate")
    private Date gmtCreate;

    /** 更新时间*/
    @Mapping("gmtModified")
    private Date gmtModified;

    /** 创建人*/
    @Mapping("creator")
    private Integer creator;

    /** 修改人*/
    @Mapping("modifier")
    private Integer modifier;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

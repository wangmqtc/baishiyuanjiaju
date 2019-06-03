package com.baishiyuan.domain;

import org.dozer.Mapping;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/7 0007.
 */
public class OrderGoods implements Serializable{
    private static final long serialVersionUID = -2060089224226499636L;

    @Mapping("id")
    private String googdsId;

    private Integer number;

    @Mapping("image")
    private String image;

    @Mapping("description")
    private String description;

    @Mapping("price")
    private Integer price;

    @Mapping("color")
    private String color;

    @Mapping("material")
    private String material;

    @Mapping("model")
    private String model;

    public String getGoogdsId() {
        return googdsId;
    }

    public void setGoogdsId(String googdsId) {
        this.googdsId = googdsId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}

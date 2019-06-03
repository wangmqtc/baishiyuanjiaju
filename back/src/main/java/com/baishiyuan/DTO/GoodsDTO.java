package com.baishiyuan.DTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class GoodsDTO implements Serializable{

    private static final long serialVersionUID = -2248144959917731417L;

    @NotEmpty(message="颜色的不能为空")
    private String color;

    /**产品名称*/
    @NotEmpty(message="图片url不能为空")
    private String image;

    /**图片地址*/
    @NotEmpty(message="材质不能为空")
    private String material;

    /**该产品材质*/
    @NotNull(message="价格不能为空")
    private Integer price;

    /**该产品名称*/
    private String name;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

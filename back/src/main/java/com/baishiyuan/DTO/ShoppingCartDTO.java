package com.baishiyuan.DTO;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ShoppingCartDTO implements Serializable{

    private static final long serialVersionUID = -8909604658183488794L;

    @NotNull(message="商品数量不能为空")
    /**当前物品的购买数量*/
    private Integer number;

    /** 商品ID*/
    @NotEmpty(message="商品ID不能为空")
    private String goodsId;

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
}

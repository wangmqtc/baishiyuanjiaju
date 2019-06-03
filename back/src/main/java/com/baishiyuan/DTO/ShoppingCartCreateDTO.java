package com.baishiyuan.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 2YVTFQ2 on 2019/4/25.
 */
public class ShoppingCartCreateDTO implements Serializable {
    private static final long serialVersionUID = -4205299113234928175L;

    @NotNull(message="参数不能为空")
    /**当前要添加到购物车的物品和数量*/
    private List<ShoppingCartDTO> goods;

    public List<ShoppingCartDTO> getGoods() {
        return goods;
    }

    public void setGoods(List<ShoppingCartDTO> goods) {
        this.goods = goods;
    }
}

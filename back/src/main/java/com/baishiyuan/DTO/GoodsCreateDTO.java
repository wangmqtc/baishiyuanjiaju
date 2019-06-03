package com.baishiyuan.DTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * Created by 2YVTFQ2 on 2019/3/28.
 */
public class GoodsCreateDTO implements Serializable{
    private static final long serialVersionUID = -5614131478063790768L;

    @NotEmpty(message="产品设置不能为空")
    @Valid
    private List<GoodsDTO> goodsDTOS;

    @NotEmpty(message="产品型号不能为空")
    private String model;

    @NotEmpty(message="系列名不能为空")
    private String publishName;

    private String details;

    public List<GoodsDTO> getGoodsDTOS() {
        return goodsDTOS;
    }

    public void setGoodsDTOS(List<GoodsDTO> goodsDTOS) {
        this.goodsDTOS = goodsDTOS;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }
}

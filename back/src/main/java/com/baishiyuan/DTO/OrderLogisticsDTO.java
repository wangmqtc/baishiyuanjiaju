package com.baishiyuan.DTO;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class OrderLogisticsDTO implements Serializable{

    private static final long serialVersionUID = 7912242770706373008L;
    @NotEmpty(message="发货物流不能为空")
    private String logistics;

    /**图片地址*/
    @NotEmpty(message="物流单号")
    private String logisticsNumber;

    /**其他备注*/
    private String remark;

    public String getLogistics() {
        return logistics;
    }

    public void setLogistics(String logistics) {
        this.logistics = logistics;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

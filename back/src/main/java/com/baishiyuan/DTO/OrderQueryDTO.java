package com.baishiyuan.DTO;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

public class OrderQueryDTO implements Serializable{
    private static final long serialVersionUID = -7426853632987415871L;
    @NotNull(message="页码不能为空")
    private Integer pageNo;

    /**产品名称*/
    @NotNull(message="页大小不能为空")
    private Integer pageSize;

    /**状态*/
    private List<Integer> status;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }
}

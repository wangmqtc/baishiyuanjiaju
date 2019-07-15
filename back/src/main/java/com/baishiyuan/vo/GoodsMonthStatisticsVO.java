package com.baishiyuan.vo;

import org.dozer.Mapping;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
public class GoodsMonthStatisticsVO implements Serializable{

    private static final long serialVersionUID = -6597808537885317624L;
    @Mapping("id")
    private String id;

    @Mapping("modelId")
    private String modelId;
    @Mapping("number")
    private Integer number;
    @Mapping("month")
    private Integer month;
    @Mapping("year")
    private Integer year;

    private String model;

    private String publishName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }
}

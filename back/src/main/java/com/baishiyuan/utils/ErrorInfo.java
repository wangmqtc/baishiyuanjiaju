package com.baishiyuan.utils;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/3/31 0031.
 */
public class ErrorInfo implements Serializable{
    private static final long serialVersionUID = -9073691727504018843L;

    public static final Integer OK = 0;
    public static final Integer ERROR = 10000;

    private Integer ret;
    private String msg;

    public Integer getRet() {
        return ret;
    }

    public void setRet(Integer ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

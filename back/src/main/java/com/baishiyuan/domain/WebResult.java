package com.baishiyuan.domain;

import com.baishiyuan.utils.StringConst;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public class WebResult implements Serializable{

    private static final long serialVersionUID = -8052768543740586828L;
    /**
     * @StringConsts
     */
    private int ret;

    private String msg;

    //任意对象
    private Object result;

    
    
    public int getRet()
    {
        return ret;
    }

    public void setRet(int ret)
    {
        this.ret = ret;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public Object getResult()
    {
        return result;
    }

    public void setResult(Object result)
    {
        this.result = result;
    }

    public WebResult(int result) {
        this.result = result;
    }

    public WebResult(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }
    
    public WebResult(int code, String msg, Object result) {
        this.ret = code;
        this.msg = msg;
        this.result = result;
    }

    /**
     * 检查返回值是否成功
     * */
    @JsonIgnore
    public boolean isReturnSuccess(){

        return this.ret == StringConst.ERRCODE_SUCCESS;
    }

    @Override
    public String toString() {
        return "WebResult{" +
                "ret=" + ret +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}

package com.baishiyuan.exception;

/**
 * 文 件 名:  MessageException.java
 */
public class MessageException extends RuntimeException{
	
    private static final long serialVersionUID = -1306120307728064187L;
    
    private int ret;
	
    private String msg;
	
	public MessageException(int ret, String msg){
	    super(msg);
	    
		this.ret = ret;
		this.msg = msg;
	}
	
	public MessageException(int ret, String msg, Throwable cause){
        this.ret = ret;
        this.msg = msg;
        
        super.initCause(cause);
    }
	
	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	

}

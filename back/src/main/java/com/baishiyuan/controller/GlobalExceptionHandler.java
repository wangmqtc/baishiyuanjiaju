package com.baishiyuan.controller;

import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.ErrorInfo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2019/3/31 0031.
 */
@ControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorInfo defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        ErrorInfo r = new ErrorInfo();
        if(e!=null && e instanceof MessageException) {
            r.setMsg(((MessageException) e).getMsg());
            r.setRet(((MessageException) e).getRet());
        }else {
            r.setMsg(e.getMessage());
            r.setRet(ErrorInfo.ERROR);
        }

        return r;
    }

}

package com.baishiyuan.controller;

import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.Utils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    private static final Logger logger = Logger.getLogger(BaseController.class);

    /** 验证码校验*/
    public Map<String,Object> checkVerifyCode(HttpServletRequest request) {
        Map<String,Object> retMap = new HashMap<>();
        String verifyCode = request.getParameter("verifyCode");

        if (request.getSession() == null ) {
            return Utils.putReturnMap(retMap, StringConst.ERRCODE_X, "用户session为空！");
        } else {
            if (! StringUtils.hasText(verifyCode)) {
                return Utils.putReturnMap(retMap, StringConst.ERRCODE_X, "验证码不能为空！");
            } else {
                if (request.getSession().getAttribute("verifyCode") != null) {
                    String verifyCodeInSession = (String) request.getSession().getAttribute("verifyCode");
                    //logger.info("verify code from request" + verifyCode);
                    //logger.info("verify code from session" + verifyCodeInSession);
                    if (! verifyCodeInSession.equalsIgnoreCase(verifyCode)) {
                        HttpSession session = request.getSession(false);
                        session.setAttribute("verifyCode", null);
                        return Utils.putReturnMap(retMap, StringConst.ERRCODE_X, "验证码不正确！");
                    } else {
                        //将验证码置为失效
                        HttpSession session = request.getSession(false);
                        session.setAttribute("verifyCode", null);
                        return null;
                    }
                } else {
                    System.out.println("session = " + request.getSession().getId());
                    return Utils.putReturnMap(retMap, StringConst.ERRCODE_X, "请重新输入验证码！");
                }
            }
        }
    }


}

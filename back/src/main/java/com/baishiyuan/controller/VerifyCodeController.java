package com.baishiyuan.controller;

import com.baishiyuan.utils.VerifyCodeUtil;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@RestController
@RequestMapping("/authImage")
public class VerifyCodeController {

    private static final Logger logger = Logger.getLogger(VerifyCodeController.class);

    @RequestMapping("")
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");

        //生成随机字串
        String verifyCode = VerifyCodeUtil.generateVerifyCode(4);
        //存入会话session
        HttpSession session = request.getSession(true);
        session.setAttribute("verifyCode", verifyCode.toLowerCase());
        System.out.println("session=" + session.getId() + ",verifyCode=" + verifyCode);
        //生成图片
        int w = 200, h = 80;

        VerifyCodeUtil.outputImage(w, h, response.getOutputStream(), verifyCode);

    }

}

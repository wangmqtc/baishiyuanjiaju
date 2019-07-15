package com.baishiyuan.controller;

import com.baishiyuan.component.ClientBenefitComponent;
import com.baishiyuan.domain.SessionInfo;
import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.AuthorityUtils;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.UserSessionFunCallUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/intf/{channel}/clientBenefit")
public class ClientBenefitController {

    private static final Logger logger = Logger.getLogger(ClientBenefitController.class);

    @Resource
    private ClientBenefitComponent clientBenefitComponent;

    @ResponseBody
    @RequestMapping("/setDiscount")
    public WebResult setDiscount(@RequestParam int userId, @RequestParam double discount, HttpServletRequest request, HttpServletResponse response){
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkClientBenefitAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        int creator = sessionInfo.getUserId();
        clientBenefitComponent.setDisCount(userId, creator, discount);

        return new WebResult(0, "设置折扣成功", 1);
    }

    @ResponseBody
    @RequestMapping("/setUrgentCount")
    public WebResult setUrgentCount(@RequestParam int userId, @RequestParam int urgentCount, HttpServletRequest request, HttpServletResponse response){
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkClientBenefitAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        int creator = sessionInfo.getUserId();
        clientBenefitComponent.setUrgentCount(userId, creator, urgentCount);

        return new WebResult(0, "设置加急次数成功", 1);
    }

    @ResponseBody
    @RequestMapping("/cancelBenefit")
    public WebResult cancelBenefit(@RequestParam int userId, @RequestParam int type, HttpServletRequest request, HttpServletResponse response){
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkClientBenefitAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        int creator = sessionInfo.getUserId();
        clientBenefitComponent.cancelBenefit(type, userId, creator);

        return new WebResult(0, "取消用户优惠权限成功", 1);
    }

    @ResponseBody
    @RequestMapping("/queryBenefits")
    public WebResult queryBenefits(@RequestParam int pageNo, @RequestParam int pageSize, @RequestParam int type, HttpServletRequest request, HttpServletResponse response){
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkClientBenefitAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Page page = clientBenefitComponent.getUserBenefits(pageNo, pageSize, type);

        return new WebResult(0, "查询成功", page);
    }

    @ResponseBody
    @RequestMapping("/checkRemainingUrgentCount")
    public WebResult checkRemainingUrgentCount(HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        Integer remaining = clientBenefitComponent.checkRemainingUrgentCount(sessionInfo.getUserId());
        return new WebResult(0, "查询成功", remaining);
    }

}

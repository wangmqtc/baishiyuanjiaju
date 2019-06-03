package com.baishiyuan.controller;

import com.baishiyuan.component.UserAccountComponent;
import com.baishiyuan.component.UserInfoComponent;
import com.baishiyuan.domain.SessionInfo;
import com.baishiyuan.domain.UserInfo;
import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/intf/{channel}/user")
public class UserController extends BaseController{

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Resource
    private UserAccountComponent userAccountComponent;

    @Resource
    private UserInfoComponent userInfoComponent;

    /**
     * 注册
     * */
    @ResponseBody
    @RequestMapping("/regist")
    public WebResult regist(UserInfo userInfo, @PathVariable String channel,
                            HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if(sessionInfo == null){
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if(sessionInfo.getType() == null){
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if(!AuthorityUtils.checkUserInfoAuth(sessionInfo.getType(), sessionInfo.getAuthority())){
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        if(userInfo == null){
            throw new MessageException(StringConst.ERRCODE_X, "传入的参数为空");
        }

        if(StringUtils.isEmpty(userInfo.getPasswd())){
            throw new MessageException(StringConst.ERRCODE_X, "密码不能为空");
        }

        checkUserInfo(userInfo);

        String nickname = WapUtil.getUserNickNameSafeFilter(userInfo.getNickName());

        if(! Utils.isReturnSuccess(WapUtil.checkPwd(userInfo.getPasswd()))){
            throw new MessageException(StringConst.ERRCODE_X, "密码应包含数字、字母！");
        }

        //是否已注册过
        UserInfo userInfo1 = userInfoComponent.getUserInfoByNickName(nickname);
        if(userInfo1 != null){
            throw new MessageException(StringConst.ERRCODE_X, "此用户名已注册过了！");
        }

        if(userInfo.getType() != 3 && userInfo.getAuthority() == null){
            userInfo.setAuthority(64);
        }

        try {
            userRegister(userInfo);
        } catch (Exception e) {
            throw new MessageException(StringConst.ERRCODE_X, e.getMessage());
        }

        boolean isAccountExists = userAccountComponent.isExists(userInfo.getUserId());
        if(!isAccountExists){
            userAccountComponent.createPersonAccount(userInfo.getUserId(), sessionInfo.getUserId());
        }

        return new WebResult(StringConst.ERRCODE_SUCCESS, "添加用户成功", 1);
    }

    /**
     * <p>用户名注册</p>
     **/
    public synchronized static boolean userRegister(UserInfo userInfo) throws Exception{
        if(!StringUtils.hasText(userInfo.getLogoId())){
            userInfo.setLogoId(null);
        }
        if(!StringUtils.hasText(userInfo.getRealName())){
            userInfo.setRealName(null);
        }
        userInfo.setGmtCreate(Calendar.getInstance().getTime());

        int userId = 1;
        UserInfoComponent userInfoComponent = (UserInfoComponent) SpringBeanUtil.getBean("userInfoComponent");
        Integer maxUserId = userInfoComponent.getUserMaxId();
        if(maxUserId != null){
            userId = maxUserId + 1;
        }
        userInfo.setUserId(userId);

        userInfo.setIsFrozen(0);
        userInfo.setPasswd(userPwdEncryption(userInfo.getPasswd()));
        userInfo.setIsDeleted(0);

        userInfo.setType(2);

        userInfoComponent.addUserInfo(userInfo);
        return true;
    }

    /**用户密码加密*/
    public static String userPwdEncryption(String str) throws Exception
    {
        //重要，不要随意更改
        return Utils.afterMd5(Utils.beforeMd5(str));
    }

    /**
     * 登录 */
    @ResponseBody
    @RequestMapping("/login")
    public WebResult login(@RequestParam String nickName, @RequestParam String pwd, @PathVariable String channel,
                           HttpServletRequest request, HttpServletResponse response) {

        //验证码校验
        Map<String,Object> verifyMap = checkVerifyCode(request);
        if (verifyMap != null) {
            return new WebResult(StringConst.ERRCODE_MUSTLOGIN, "验证码错误", 0);
        }

        if(StringUtils.isEmpty(pwd)){
            throw new MessageException(StringConst.ERRCODE_X, "密码不能为空！");
        }
        if(StringUtils.isEmpty(nickName)){
            throw new MessageException(StringConst.ERRCODE_X, "昵称不能为空！");
        }

        UserInfo userInfo = userInfoComponent.getUserInfoByNickName(nickName);

        if(userInfo != null){
            String afterMd5 = "";
            try {
                afterMd5 = Utils.afterMd5(Utils.beforeMd5(pwd));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(!afterMd5.equals(userInfo.getPasswd())){
                //登录失败
                throw new MessageException(StringConst.ERRCODE_X, "密码不正确");
            }

            createSesion(request, userInfo);

            Map<String, Object> result = new HashMap();
            result.put("type", userInfo.getType());
            result.put("userId", userInfo.getUserId());
            result.put("authority", userInfo.getAuthority());
            return new WebResult(StringConst.ERRCODE_SUCCESS, "登陆成功", result);
        }

        return new WebResult(StringConst.ERRCODE_X, "手机号码未注册", 0);
    }

    private void createSesion(HttpServletRequest request, UserInfo userInfo){
        HttpSession session = request.getSession();

        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(userInfo.getUserId());
        sessionInfo.setType(userInfo.getType());
        sessionInfo.setIsFrozen(userInfo.getIsFrozen());
        sessionInfo.setNickName(userInfo.getNickName());
        sessionInfo.setAuthority(userInfo.getAuthority());
        sessionInfo.setMbn(userInfo.getMbn());

        session.setAttribute("userInfo", sessionInfo);
    }

    @ResponseBody
    @RequestMapping("/testLogin")
    public WebResult login(@PathVariable String channel,
                           HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if(sessionInfo == null){
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        return new WebResult(StringConst.ERRCODE_SUCCESS, "登陆成功", sessionInfo);
    }

    @ResponseBody
    @RequestMapping("/updatePwd")
    public WebResult updatePwd(@RequestParam int userId, @RequestParam String pwd, @PathVariable String channel,
                               HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if(sessionInfo == null){
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if(sessionInfo.getType() == null){
            throw new MessageException(StringConst.ERRCODE_SUCCESS, "你的类型为空！");
        }

        /**判断权限*/
        if(!AuthorityUtils.checkUserInfoAuth(sessionInfo.getType(), sessionInfo.getAuthority())){
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        updatePwdMethod(pwd, userId);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "密码修改成功", 1);
    }


    @ResponseBody
    @RequestMapping("/updatePwdByMyself")
    public WebResult updatePwdByMyself(@RequestParam String pwd, @PathVariable String channel,
                                       HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if(sessionInfo == null){
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        int userId = sessionInfo.getUserId();

        updatePwdMethod(pwd, userId);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "密码修改成功", 1);
    }


    private void updatePwdMethod(String pwd, int userId){
        if(StringUtils.isEmpty(pwd)){
            throw new MessageException(StringConst.ERRCODE_X, "密码不能为空");
        }

        if(! Utils.isReturnSuccess(WapUtil.checkPwd(pwd))){
            throw new MessageException(StringConst.ERRCODE_X, "密码应包含数字、字母！");
        }

        String pwdMd5 = "";
        try {
            pwdMd5 = userPwdEncryption(pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserInfo userInfo = userInfoComponent.updatePwd(userId, pwdMd5);
        if(userInfo == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }
    }

    @ResponseBody
    @RequestMapping("/fronzeUser")
    public WebResult fronzeUser(@RequestParam int userId, @RequestParam int type,
                                HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_SUCCESS, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserInfoAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        UserInfo userInfo = null;
        if(type == 0){
            /**解封用户*/
            userInfo = userInfoComponent.fronzeUser(userId, type);
        }else if(type == 1){
            userInfo = userInfoComponent.fronzeUser(userId, type);
        }else{
            throw new MessageException(StringConst.ERRCODE_X, "参数type类型出错！");
        }

        if(userInfo == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }
        return new WebResult(StringConst.ERRCODE_SUCCESS, "操作成功", 1);
    }


    @ResponseBody
    @RequestMapping("/editUserInfo")
    public WebResult editUserInfo(UserInfo userInfo,
                                  HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_SUCCESS, "你的类型为空！");
        }
        /**判断权限*/
        if (!AuthorityUtils.checkUserInfoAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        if(userInfo == null){
            throw new MessageException(StringConst.ERRCODE_X, "传入的参数为空");
        }

        if(userInfo.getUserId() == null){
            throw new MessageException(StringConst.ERRCODE_X, "用户ID不能为空");
        }

        checkUserInfo(userInfo);

        userInfoComponent.updateUserInfo(userInfo);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "修改成功", 1);
    }

    @ResponseBody
    @RequestMapping("/editUserInfoByMyself")
    public WebResult editUserInfoByMyself(UserInfo userInfo,
                                          HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if(userInfo == null){
            throw new MessageException(StringConst.ERRCODE_X, "传入的参数为空");
        }

        userInfo.setUserId(sessionInfo.getUserId());

        if(StringUtils.isEmpty(userInfo.getNickName())){
            throw new MessageException(StringConst.ERRCODE_X, "昵称不能为空");
        }

        if(StringUtils.isEmpty(userInfo.getMbn())){
            throw new MessageException(StringConst.ERRCODE_X, "手机号不能为空");
        }

        if(!StringUtils.isEmpty(userInfo.getMbn()) && !WapUtil.isMobileno(userInfo.getMbn())){
            throw new MessageException(StringConst.ERRCODE_X, "手机号填写错误");
        }

        userInfoComponent.updateUserInfoByMyself(userInfo);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "修改成功", 1);
    }


    private void checkUserInfo(UserInfo userInfo){
        if(StringUtils.isEmpty(userInfo.getNickName())){
            throw new MessageException(StringConst.ERRCODE_X, "昵称不能为空");
        }

        if(StringUtils.isEmpty(userInfo.getMbn())){
            throw new MessageException(StringConst.ERRCODE_X, "手机号不能为空");
        }

        if(!StringUtils.isEmpty(userInfo.getMbn()) && !WapUtil.isMobileno(userInfo.getMbn())){
            throw new MessageException(StringConst.ERRCODE_X, "手机号填写错误");
        }

        if(userInfo.getType() == null){
            throw new MessageException(StringConst.ERRCODE_X, "没有填写用户类型");
        }

        List<Integer> types = new ArrayList<>();
        types.add(0);types.add(1);types.add(2);types.add(3);
        if(!types.contains(userInfo.getType())){
            throw new MessageException(StringConst.ERRCODE_X, "填写的用户类型不正确");
        }

        if(userInfo.getType() == 3){
            if(userInfo.getAuthority() == null || userInfo.getAuthority() > 128 || userInfo.getAuthority() < 0){
                throw new MessageException(StringConst.ERRCODE_X, "子超级管理员的权限配置有问题");
            }
        }
    }

    @ResponseBody
    @RequestMapping("/queryUserInfos")
    public WebResult queryUserInfos(@RequestParam int pageNo, @RequestParam int pageSize,
                                    HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_SUCCESS, "你的类型为空！");
        }
        /**判断权限*/
        if (!AuthorityUtils.checkUserInfoAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Page page = userInfoComponent.getUserInfos(pageNo, pageSize);
        if(CollectionUtils.isEmpty(page.getList())){
            page.setList(new ArrayList());
        }else{
            List<UserInfo> userInfos = page.getList();
            userInfos.stream().forEach(s -> {
                s.setPasswd(null);
            });
        }
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @ResponseBody
    @RequestMapping("/querySingleUserInfo")
    public WebResult querySingleUserInfo(@RequestParam String id,
                                         HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_SUCCESS, "你的类型为空！");
        }
        /**判断权限*/
        if (!AuthorityUtils.checkUserInfoAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        UserInfo userInfo = userInfoComponent.getSingleUserInfoById(id);
        userInfo.setPasswd(null);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", userInfo);
    }

    @ResponseBody
    @RequestMapping("/queryUserInfoByMySelf")
    public WebResult queryUserInfoByMySelf(HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(sessionInfo.getUserId());
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", userInfo);
    }

    /**
     * 退出登录
     * */
    @ResponseBody
    @RequestMapping("/logOut")
    public WebResult logOut(HttpServletRequest request, HttpServletResponse response) throws Exception {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            return new WebResult(StringConst.ERRCODE_MUSTLOGIN, "你没有登录", 0);
        }

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        HttpSession httpSession = request.getSession(false);

        if(httpSession != null){
            httpSession.removeAttribute("userInfo");
        }

        return new WebResult(StringConst.ERRCODE_SUCCESS, "登出成功", 1);
    }

    @ResponseBody
    @RequestMapping("/checkIsLogin")
    public WebResult checkIsLogin(HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        Map<String, Object> map = new HashMap<>();
        if (sessionInfo == null) {
            map.put("isLogin", 0);
            return new WebResult(StringConst.ERRCODE_SUCCESS, "用户未登录", map);
        }

        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(sessionInfo.getUserId());
        map.put("isLogin", 1);
        map.put("authority", userInfo.getAuthority());
        return new WebResult(StringConst.ERRCODE_SUCCESS, "用户登录", map);
    }
}

package com.baishiyuan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.component.UserAccountComponent;
import com.baishiyuan.component.UserInfoComponent;
import com.baishiyuan.domain.*;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.*;
import com.baishiyuan.vo.UserAccountFlowVO;
import com.baishiyuan.vo.UserAccountVO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/userAccount")
public class UserAccountController extends BaseController {

    private static final Logger logger = Logger.getLogger(UserAccountController.class);

    @Resource
    private UserAccountComponent userAccountComponent;

    @Resource
    private UserInfoComponent userInfoComponent;

    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;

    /**
     * 分页查看用户账户
     */
    @ResponseBody
    @RequestMapping("/queryUserAccountByPage")
    public WebResult queryUserAccountByPage(@RequestParam int pageNo, @RequestParam int pageSize, HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Page page = userAccountComponent.queryUserAccount(pageNo, pageSize, null);
        if (!CollectionUtils.isEmpty(page.getList())) {
            List<UserAccount> userAccounts = page.getList();
            List<UserAccountVO> userAccountVOs = new ArrayList<>();

            List<Integer> userIds = new ArrayList<>();
            for (UserAccount userAccount : userAccounts) {
                userIds.add(userAccount.getUserId());
            }

            Map<Integer, UserInfo> userInfoMap = userInfoComponent.getUserInfosByUserIds(userIds);

            for (UserAccount userAccount : userAccounts) {
                Map<String, Object> map = JavaBeanUtils.convertBeanToMap(userAccount);
                UserAccountVO userAccountVO = new UserAccountVO();
                try {
                    BeanUtils.populate(userAccountVO, map);
                } catch (Exception e) {
                    throw new MessageException(StringConst.ERRCODE_X, "转换错误");
                }

                userAccountVO.setAvailableAssets(new Double(userAccount.getAvailableAssets())/100);
                userAccountVO.setFrozenAssets(new Double(userAccount.getFrozenAssets())/100);
                userAccountVO.setTotalAssets(new Double(userAccount.getTotalAssets())/100);

                UserInfo userInfo = userInfoMap.get(userAccount.getUserId());
                if (userInfo != null) {
                    userAccountVO.setNickName(userInfo.getNickName());
                    userAccountVO.setLogoId(userInfo.getLogoId());
                }
                userAccountVOs.add(userAccountVO);
            }
            page.setList(userAccountVOs);

        }
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }


    /**
     * 增加用户账户
     */
    @ResponseBody
    @RequestMapping("/addUserAccount")
    public WebResult addUserAccount(@RequestParam int userId, @RequestParam int type, @RequestParam double money, @RequestParam String reason, String eventId,
                                    HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        if(StringUtils.isEmpty(reason)){
            throw new MessageException(StringConst.ERRCODE_X, "没有填写原因备注");
        }

        int changeMoney = (int)(money*100);
        userAccountComponent.addMoney(sessionInfo.getUserId(), userId, changeMoney, reason, type, eventId);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "加钱成功", 1);
    }


    @ResponseBody
    @RequestMapping("/scanUndefine")
    public WebResult scanUndefine(HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        List<UserInfo> userInfos = mongoTemplate.find(query, UserInfo.class);

        List<Integer> userIds = new ArrayList<>();
        for(UserInfo userInfo : userInfos){
            userIds.add(userInfo.getUserId());
        }

        Query query1 = new Query();
        query1.addCriteria(Criteria.where("isDeleted").is(0));
        List<UserAccount> userAccounts = mongoTemplate.find(query, UserAccount.class);
        List<Integer> userIds1 = new ArrayList<>();
        for(UserAccount userAccount : userAccounts){
            userIds1.add(userAccount.getUserId());
        }

        userIds.remove(userIds1);
        for(int userId : userIds){
            boolean isAccountExists = userAccountComponent.isExists(userId);
            if(!isAccountExists){
                userAccountComponent.createPersonAccount(userId, sessionInfo.getUserId());
            }
        }
        return new WebResult(StringConst.ERRCODE_SUCCESS, "添加账户金额成功", 1);
    }


    /**
     * 分页查看用户自己的账户
     */
    @ResponseBody
    @RequestMapping("/queryUserSelfAccountByPage")
    public WebResult queryUserSelfAccountByPage(@RequestParam int pageNo, @RequestParam int pageSize,
                                                HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        int userId = sessionInfo.getUserId();

        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(userId);

        Page page = userAccountComponent.queryUserAccountFlowByPageByUserId(userId, pageNo, pageSize);
        if (!CollectionUtils.isEmpty(page.getList())) {
            List<UserAccountFlow> userAccountFlows = page.getList();
            List<UserAccountFlowVO> userAccountFlowVOS = new ArrayList<>();

            for (UserAccountFlow userAccountFlow : userAccountFlows) {
                Map<String, Object> map = JavaBeanUtils.convertBeanToMap(userAccountFlow);
                UserAccountFlowVO userAccountFlowVO = new UserAccountFlowVO();
                try {
                    BeanUtils.populate(userAccountFlowVO, map);
                } catch (Exception e) {
                    throw new MessageException(StringConst.ERRCODE_X, "转换错误");
                }

                userAccountFlowVO.setChangeMoney(new Double(userAccountFlow.getChangeMoney())/100);
                userAccountFlowVO.setCurrentMoney(new Double(userAccountFlow.getCurrentMoney())/100);
                userAccountFlowVO.setPrevMoney(new Double(userAccountFlow.getPrevMoney())/100);

                userAccountFlowVO.setNickName(userInfo.getNickName());
                userAccountFlowVO.setLogoId(userInfo.getLogoId());
                userAccountFlowVOS.add(userAccountFlowVO);
            }
            page.setList(userAccountFlowVOS);

        }

        UserAccount userAccount = userAccountComponent.queryUserAccountByUserId(userId);
        if(userAccount.getTotalAssets() == null){
            throw new MessageException(StringConst.ERRCODE_X, "个人账户余额为空");
        }
        if(userAccount.getAvailableAssets() == null){
            throw new MessageException(StringConst.ERRCODE_X, "个人账户可用余额为空");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNo", page.getPageNo());
        jsonObject.put("pageSize", page.getPageSize());
        jsonObject.put("list", page.getList());
        jsonObject.put("totalCount", page.getTotalCount());
        jsonObject.put("totalPage", page.getTotalPage());
        jsonObject.put("startPos", page.getStartPos());
        jsonObject.put("totalAssets", new Double(userAccount.getTotalAssets())/100);
        jsonObject.put("availableAssets", new Double(userAccount.getAvailableAssets())/100);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", jsonObject);
    }


    /**
     * 查看总收入或者总支出
     */
    @ResponseBody
    @RequestMapping("/queryAllMoney")
    public WebResult queryAllMoney(@RequestParam int operation, @RequestParam int pageNo, @RequestParam int pageSize,
                                   HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountScanAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Page page = userAccountComponent.queryAllFlow(operation, pageNo, pageSize);
        if (!CollectionUtils.isEmpty(page.getList())) {
            List<UserAccountFlow> userAccountFlows = page.getList();
            List<UserAccountFlowVO> userAccountFlowVOS = new ArrayList<>();
            Set<Integer> userIdSet = new HashSet<>();
            List<Integer> userIds = new ArrayList<>();
            for (UserAccountFlow userAccountFlow : userAccountFlows) {
                userIdSet.add(userAccountFlow.getUserId());
            }
            userIds.addAll(userIdSet);

            Map<Integer, UserInfo> userInfoMap = userInfoComponent.getUserInfosByUserIds(userIds);

            for (UserAccountFlow userAccountFlow : userAccountFlows) {
                Map<String, Object> map = JavaBeanUtils.convertBeanToMap(userAccountFlow);
                UserAccountFlowVO userAccountFlowVO = new UserAccountFlowVO();
                try {
                    BeanUtils.populate(userAccountFlowVO, map);
                } catch (Exception e) {
                    throw new MessageException(StringConst.ERRCODE_X, "转换错误");
                }

                userAccountFlowVO.setChangeMoney(new Double(userAccountFlow.getChangeMoney())/100);
                userAccountFlowVO.setCurrentMoney(new Double(userAccountFlow.getCurrentMoney())/100);
                userAccountFlowVO.setPrevMoney(new Double(userAccountFlow.getPrevMoney())/100);

                UserInfo userInfo = userInfoMap.get(userAccountFlow.getUserId());
                if (userInfo != null) {
                    userAccountFlowVO.setNickName(userInfo.getNickName());
                    userAccountFlowVO.setLogoId(userInfo.getLogoId());
                }

                userAccountFlowVOS.add(userAccountFlowVO);
            }
            page.setList(userAccountFlowVOS);
        }

        Integer allMoney = userAccountComponent.getAllNumber(operation);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pageNo", page.getPageNo());
        jsonObject.put("pageSize", page.getPageSize());
        jsonObject.put("list", page.getList());
        jsonObject.put("totalCount", page.getTotalCount());
        jsonObject.put("totalPage", page.getTotalPage());
        jsonObject.put("startPos", page.getStartPos());
        jsonObject.put("allMoney", new Double(allMoney)/100);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", jsonObject);
    }

    /**
     * 根据月份查看盈余
     */
    @ResponseBody
    @RequestMapping("/getFlowsByMonths")
    public WebResult getFlowsByMonths(@RequestParam int pageNo, @RequestParam int pageSize,
                                      HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountScanAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Page page = userAccountComponent.getFlowsByMonth(pageNo, pageSize);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    /**
     * 根据年份查看盈余
     */
    @ResponseBody
    @RequestMapping("/getFlowsByYears")
    public WebResult getFlowsByYears(@RequestParam int pageNo, @RequestParam int pageSize,
                                     HttpServletRequest request, HttpServletResponse response) {

        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountScanAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        Page page = userAccountComponent.getFlowsByYear(pageNo, pageSize);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    /**
     * 增加用户账户
     */
    @ResponseBody
    @RequestMapping("/substractMoney")
    public WebResult substractMoney(@RequestParam int userId, @RequestParam double money, @RequestParam String reason,
                                    HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkUserAccountAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        if(StringUtils.isEmpty(reason)){
            throw new MessageException(StringConst.ERRCODE_X, "没有填写原因备注");
        }

        int changeMoney = (int)(money*100);
        userAccountComponent.substractMoney(sessionInfo.getUserId(), userId, changeMoney, reason, 4, null);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "扣除成功", 1);
    }

}

package com.baishiyuan.controller;

import com.baishiyuan.component.OrderComponent;
import com.baishiyuan.component.UserInfoComponent;
import com.baishiyuan.domain.*;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.UserSessionFunCallUtil;
import com.baishiyuan.vo.OrderVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@RestController
@RequestMapping("/intf/{channel}/order")
public class OldOrderController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private OrderComponent orderComponent;

    @Resource
    private UserInfoComponent userInfoComponent;

    @ResponseBody
    @RequestMapping("/querySelfOrders")
    public WebResult querySelfOrders(@RequestParam int status, @RequestParam int pageNo, @RequestParam int pageSize, HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = getSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_SUCCESS, "你的类型为空！");
        }

        int sessionInfoUserId = sessionInfo.getUserId();

        Page page = orderComponent.getOrdersByStatusBySelf(sessionInfoUserId, status, pageNo, pageSize);
        if(!CollectionUtils.isEmpty(page.getList())){
            List<Order> orders = page.getList();
            List<OrderVO> orderVOS = new ArrayList<>();
            for(Order order : orders){
                OrderVO orderVO = new OrderVO();

                int userId = order.getUserId();

                orderVO.setUserId(order.getUserId());
                orderVO.setAddress(order.getAddress());
                orderVO.setClientName(order.getClientName());
                orderVO.setClientPhone(order.getClientPhone());
                orderVO.setCostMoney(new Double(order.getCostMoney())/100);
                orderVO.setCreator(userId);
                orderVO.setGmtCreate(order.getGmtCreate());
                orderVO.setId(order.getId());
                orderVO.setIsCancel(order.getIsCancel());
                orderVO.setIsCost(order.getIsCost());
                orderVO.setIsDeleted(0);
                orderVO.setIsDisCount(order.getIsDisCount());
                orderVO.setRemark(order.getRemark());

                List<OrderGoods> goods = order.getOrderGoodss();
                List<String> productIds = new ArrayList<>();
                List<String> productNames = new ArrayList<>();

                for(OrderGoods orderGoods : goods) {
                    productIds.add(orderGoods.getGoogdsId());
                    productNames.add(orderGoods.getModel());
                }

                orderVO.setProductIds(productIds);
                orderVO.setProductNames(productNames);
                orderVO.setCreateDay(order.getCreateDay());

                UserInfo userInfo = userInfoComponent.getUserInfoByUserId(userId);
                if(userInfo == null){
                    orderVOS.add(orderVO);
                    continue;
                }

                orderVO.setNickName(userInfo.getNickName());
                orderVO.setRealName(userInfo.getRealName());
                orderVO.setLogoId(userInfo.getLogoId());
                orderVO.setCreatorMbn(userInfo.getMbn());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                orderVO.setGmtCreateStr(sdf.format(order.getGmtCreate()));

                orderVOS.add(orderVO);
            }
            page.setList(orderVOS);
            return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
        }else{
            page.setList(new ArrayList());
            return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
        }
    }

    private SessionInfo getSession(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        return sessionInfo;
    }

}

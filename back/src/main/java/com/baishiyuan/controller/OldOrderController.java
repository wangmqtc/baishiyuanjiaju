package com.baishiyuan.controller;

import com.baishiyuan.component.OrderComponent;
import com.baishiyuan.domain.OrderGoods;
import com.baishiyuan.domain.SessionInfo;
import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.UserSessionFunCallUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@RestController
@RequestMapping("/intf/{channel}/order")
public class OldOrderController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private OrderComponent orderComponent;

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

        List<Map<String, Object>> maps = page.getList();

        for(Map<String, Object> map : maps) {
            String buyGoods = "";
            List<OrderGoods> orderGoodss = (List<OrderGoods>)map.get("orderGoodss");
            if(CollectionUtils.isEmpty(orderGoodss)) {
                map.put("buyGoods", buyGoods);
                continue;
            }
            for(OrderGoods orderGoods : orderGoodss) {
                buyGoods += orderGoods.getModel() + "_" + orderGoods.getMaterial() + "_" + orderGoods.getColor() + " * " + orderGoods.getNumber() + ",";
            }
            if(!StringUtils.isEmpty(buyGoods)) {
                buyGoods = buyGoods.substring(0, buyGoods.length() - 1);
            }
            map.put("buyGoods", buyGoods);
        }

        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    private SessionInfo getSession(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        return sessionInfo;
    }

}

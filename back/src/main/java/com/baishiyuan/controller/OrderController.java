package com.baishiyuan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.DTO.OrderQueryDTO;
import com.baishiyuan.component.OrderComponent;
import com.baishiyuan.component.ShoppingCartComponent;
import com.baishiyuan.component.UserAccountComponent;
import com.baishiyuan.component.UserInfoComponent;
import com.baishiyuan.domain.*;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@RestController
@RequestMapping({"/order"})
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MongoTemplate mongoTemplate;

    @Resource
    private ShoppingCartComponent shoppingCartComponent;

    @Resource
    private UserAccountComponent userAccountComponent;

    @Resource
    private OrderComponent orderComponent;

    @Resource
    private UserInfoComponent userInfoComponent;

    @Autowired
    protected Mapper dozerMapper;

    @RequestMapping(value = "", method = {RequestMethod.POST})
    public WebResult order(@RequestParam String address, @RequestParam String phone, @RequestParam String name, String remark, HttpServletRequest request) {
        SessionInfo sessionInfo = testGetSession(request);

        List<ShoppingCart> shoppingCarts = shoppingCartComponent.queryShoppingCarts(sessionInfo.getUserId());
        if(CollectionUtils.isEmpty(shoppingCarts)) {
            throw new MessageException(StringConst.ERRCODE_X, "您当前的购物车是空的！");
        }

        Set<String> goodsIds = new HashSet<>();
        Map<String, Integer> goodsIdToNumber = new HashMap<>();
        List<String> shoppingCartIds = new ArrayList<>();
        for(ShoppingCart shoppingCart : shoppingCarts) {
            shoppingCartIds.add(shoppingCart.getId());
            goodsIds.add(shoppingCart.getGoodsId());
            if(goodsIdToNumber.containsKey(shoppingCart.getGoodsId())) {
                goodsIdToNumber.put(shoppingCart.getGoodsId(), goodsIdToNumber.get(shoppingCart.getGoodsId()) + shoppingCart.getNumber());
            }else {
                goodsIdToNumber.put(shoppingCart.getGoodsId(), shoppingCart.getNumber());
            }
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(goodsIds));
        List<Goods> goodss = mongoTemplate.find(query, Goods.class);
        if(CollectionUtils.isEmpty(goodss)) {
            throw new MessageException(StringConst.ERRCODE_X, "您选中的产品已经过期不存在！");
        }

        List<OrderGoods> orderGoodss = new ArrayList<>();
        int totalPrice = 0;
        for(Goods goods : goodss) {
            OrderGoods orderGoods = new OrderGoods();
            dozerMapper.map(goods, orderGoods);
            orderGoods.setNumber(goodsIdToNumber.get(goods.getId()));
            orderGoodss.add(orderGoods);

            //计算总金钱
            totalPrice += goodsIdToNumber.get(goods.getId())*goods.getPrice();
        }

        String serialNumber = "";
        try {
            OrderSerialNumber orderSerialNumber = orderComponent.getOrderSerialNumber();
            String serialNumberFormat = String.format("%04d", orderSerialNumber.getSerialNumber());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            serialNumber = sdf.format(orderSerialNumber.getDate()) + "-" + serialNumberFormat;
        } catch (Exception e) {
            throw new MessageException(StringConst.ERRCODE_X, "操作失败，请再操作一次！");
        }

        Order order = new Order();
        order.setOrderserializable(serialNumber);
        order.setUserId(sessionInfo.getUserId());
        order.setOrderGoodss(orderGoodss);
        order.setGmtCreate(new Date());
        order.setIsCancel(0);
        order.setCreator(sessionInfo.getUserId());
        order.setAddress(address);
        order.setClientName(name);
        order.setClientPhone(phone);
        order.setIsCost(1);
        order.setStatus(0);
        order.setPrintNumber(0);
        order.setRemark(remark);

        Query userAccountQuery = new Query();
        userAccountQuery.addCriteria(Criteria.where("userId").is(sessionInfo.getUserId()));
        userAccountQuery.addCriteria(Criteria.where("totalAssets").gte(totalPrice));
        userAccountQuery.addCriteria(Criteria.where("availableAssets").gte(totalPrice));
        boolean isExist = mongoTemplate.exists(userAccountQuery, UserAccount.class);
        if(!isExist) {
            throw new MessageException(StringConst.ERRCODE_X, "您的余额不够！");
        }

       JSONObject jsonObject = orderComponent.reduceMoney(totalPrice, sessionInfo.getUserId());
        mongoTemplate.insert(order);

        String userFlowId = jsonObject.getString("id");
        if(!StringUtils.isEmpty(userFlowId)) {
            orderComponent.upadateFlowRemark(userFlowId, "购买商品,订单号为:" + order.getId());
        }


        //清除购物车
        shoppingCartComponent.deleteGoodsInShoppingCartByUserId(sessionInfo.getUserId(), shoppingCartIds);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "下订单成功", 1);
    }

    @RequestMapping(value = "/{orderId}", method = {RequestMethod.PUT})
    public WebResult updateOrder(@PathVariable String orderId, @RequestParam String logistics, @RequestParam String logisticsNumber, String remark,
                                  HttpServletRequest request) {
        SessionInfo sessionInfo = testCheckOrderAuth(request);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
        query.addCriteria(Criteria.where("status").is(1));
        Update update = new Update();
        update.set("companyName", logistics);
        update.set("logisticsNumber", logisticsNumber);
        if(!StringUtils.isEmpty(remark)) {
            update.set("remark", remark);
        }

        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        Order newOrder = mongoTemplate.findAndModify(query, update, options, Order.class);
        if(newOrder == null) {
            throw new MessageException(StringConst.ERRCODE_X, "订单修改失败");
        }

        return new WebResult(StringConst.ERRCODE_SUCCESS, "修改订单成功", newOrder);
    }

    @RequestMapping(value = "/orders", method = {RequestMethod.POST})
    public WebResult queryOrders(@RequestBody @Validated OrderQueryDTO orderQueryDTO, HttpServletRequest request) {
        SessionInfo sessionInfo = testCheckOrderAuth(request);

        Page page = orderComponent.queryOrders(orderQueryDTO.getPageNo(), orderQueryDTO.getPageSize(), orderQueryDTO.getStatus());
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @RequestMapping(value = "/orderDelivery/{orderId}", method = {RequestMethod.PUT})
    public WebResult orderDelivery(@PathVariable String orderId, @RequestParam String logistics, @RequestParam String logisticsNumber, HttpServletRequest request) {
        SessionInfo sessionInfo = testCheckOrderAuth(request);

        Order newOrder = orderComponent.orderDelivery(orderId, logistics, logisticsNumber);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "已更新状态", newOrder);
    }

    @RequestMapping(value = "/orderPrint", method = {RequestMethod.GET})
    public WebResult orderPrint(@RequestParam String orderId, HttpServletRequest request) {
        SessionInfo sessionInfo = testCheckOrderAuth(request);

        orderComponent.updatePrintNumber(orderId);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "打印次数更新", 1);
    }

    @RequestMapping(value = "/{orderId}", method = {RequestMethod.GET})
    public WebResult querySingleOrder(@PathVariable String orderId, HttpServletRequest request) {
        SessionInfo sessionInfo = testCheckOrderAuth(request);

        Order order = orderComponent.queryOrderById(orderId);

        String buyGoods = "";
        List<OrderGoods> orderGoodss = order.getOrderGoodss();
        for(OrderGoods orderGoods : orderGoodss) {
            buyGoods += orderGoods.getModel() + "_" + orderGoods.getMaterial() + "_" + orderGoods.getColor() + " * " + orderGoods.getNumber() + ",";
        }
        if(!StringUtils.isEmpty(buyGoods)) {
            buyGoods = buyGoods.substring(0, buyGoods.length() - 1);
        }
        order.setBuyGoods(buyGoods);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", order);
    }

    @RequestMapping(value = "/orderExort/{orderId}", method = {RequestMethod.GET})
    public void orderExort(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = testCheckOrderAuth(request);

        int userId = sessionInfo.getUserId();
        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(userId);
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此用户");
        }

        Order order = orderComponent.getSingleOrderById(orderId);
        if(order == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此订单");
        }

        Map<String, Object> dataMap = orderComponent.createDoc(order, userInfo);

        //给当前的订单改变状态，从0变成1
        orderComponent.changeOrderStatus(orderId);

        String fileName = "order_" + orderId + ".xlsx";
        try {
            byte[] bytes = fileName.getBytes("gb2312");
            fileName = new String(bytes, "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try
        {
//            response.setContentType("application/msexcel;charset=utf-8");
//            response.setContentType("application/force-download");
            response.setContentType("application/octet-stream");
//            response.setContentType("application/download");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            //创建配置实例
            Configuration configuration = new Configuration();

            //设置编码
            configuration.setDefaultEncoding("UTF-8");

            //ftl模板文件
            configuration.setClassForTemplateLoading(WordUtil.class,"/");

            //获取模板
            Template template = configuration.getTemplate("./example.ftl");

            //将模板和数据模型合并生成文件
            Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),"utf-8"));
            //生成文件
            template.process(dataMap, out);

            response.getOutputStream().flush();
            response.getOutputStream().close();
            //关闭流
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private SessionInfo getSession(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        return sessionInfo;
    }

    private SessionInfo testCheckOrderAuth(HttpServletRequest request) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(1);

        return sessionInfo;
    }

    private SessionInfo testGetSession(HttpServletRequest request) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(1);

        return sessionInfo;
    }

    private SessionInfo checkOrderAuth(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkClientOrderAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        return sessionInfo;
    }

}

package com.baishiyuan.component;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.domain.*;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.JavaBeanUtils;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.Utils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2019/3/31 0031.
 */
@Component("orderComponent")
public class OrderComponent {
    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    protected Mapper dozerMapper;

    public Order queryOrderById(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
        Order order = mongoTemplate.findOne(query, Order.class);
        if(order == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有找到相应的订单");
        }
        if(order.getUserId() != null) {
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("userId").is(order.getUserId()));
            query1.addCriteria(Criteria.where("isDeleted").is(0));
            UserInfo user = mongoTemplate.findOne(query1, UserInfo.class);
            if(user != null) {
                order.setNickName(user.getNickName());
            }
        }
        return order;
    }

    public OrderSerialNumber getOrderSerialNumber() {
        Date now = Calendar.getInstance().getTime();
        int day = Utils.formatIntDay(Calendar.getInstance());
        Query query = new Query();
        query.addCriteria(Criteria.where("day").is(day));
        Update update = new Update();
        update.inc("serialNumber", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        OrderSerialNumber orderSerialNumber = mongoTemplate.findAndModify(query, update, options, OrderSerialNumber.class);
        if(orderSerialNumber == null) {
            orderSerialNumber = new OrderSerialNumber();
            orderSerialNumber.setDay(day);
            orderSerialNumber.setDate(now);
            orderSerialNumber.setSerialNumber(0);
            mongoTemplate.insert(orderSerialNumber);
        }
        return orderSerialNumber;
    }

    public Page queryOrders(int pageNo, int pageSize, List<Integer> status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("status").in(status));
        Long totalNum = mongoTemplate.count(query, Order.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        List<Order> orders = mongoTemplate.find(query, Order.class);

        List<Map<String, Object>> orderVOS = convertOrderToOrderVOs(orders);
        page.setList(orderVOS);
        return page;
    }

    public JSONObject reduceMoney(int totalPrize, int userId, String clientName){

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("totalAssets").gte(totalPrize));
        query.addCriteria(Criteria.where("availableAssets").gte(totalPrize));
        int recudeMoney = totalPrize*(-1);
        Update update = new Update();
        update.inc("totalAssets", recudeMoney);
        update.inc("availableAssets", recudeMoney);
        UserAccount userAccount = mongoTemplate.findAndModify(query, update, UserAccount.class);
        if(userAccount == null){
            throw new MessageException(StringConst.ERRCODE_X, "账户资金不足");
        }

        /**扣除账户资金生成流水*/
        UserAccountFlow userAccountFlow = new UserAccountFlow();
        userAccountFlow.setGmtCreate(Calendar.getInstance().getTime());
        userAccountFlow.setUserId(userId);
        userAccountFlow.setIsDeleted(0);
        userAccountFlow.setCreaDay(Utils.formatIntDay(Calendar.getInstance()));
        userAccountFlow.setChangeMoney(totalPrize);
        userAccountFlow.setPrevMoney(userAccount.getAvailableAssets());
        userAccountFlow.setCurrentMoney(userAccount.getAvailableAssets() + recudeMoney);
        userAccountFlow.setChangeReasonType(0);
        userAccountFlow.setOperation(1);
        userAccountFlow.setReason("购买商品");
        userAccountFlow.setCreator(userId);
        userAccountFlow.setClinetName(clientName);
        mongoTemplate.insert(userAccountFlow);

        JSONObject result = new JSONObject();
        result.put("id", userAccountFlow.getId());
        result.put("cost", totalPrize);
        return result;
    }

    public void upadateFlowRemark(String id, String reason) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        Update update = new Update();
        update.set("reason", reason);
        mongoTemplate.findAndModify(query, update, UserAccountFlow.class);
    }

    public boolean updatePrintNumber(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
        Update update = new Update();
        update.inc("printNumber", 1);

        mongoTemplate.findAndModify(query, update, Order.class);

        return true;
    }

    public Order getSingleOrderById(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
        Order order = mongoTemplate.findOne(query, Order.class);
        return order;
    }

    public Page getOrdersByStatusBySelf(Integer userId, int status, int pageNo, int pageSize) {
        Query query = new Query();
        if(status == 0){
            query.addCriteria(Criteria.where("status").is(0));
        }else if(status == 1){
            query.addCriteria(Criteria.where("status").is(1));
        }else if(status == 2){
            query.addCriteria(Criteria.where("status").is(2));
        }

        if(userId != null){
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        Long totalNum = mongoTemplate.count(query, Order.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.ASC ,"gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        List<Order> orders = mongoTemplate.find(query, Order.class);
        List<Map<String, Object>> orderVOS = convertOrderToOrderVOs(orders);
        page.setList(orderVOS);

        return page;
    }

    private List<Map<String, Object>> convertOrderToOrderVOs(List<Order> orders) {
        List<Integer> userIds = new ArrayList<>();
        List<Map<String, Object>> orderVOS = new ArrayList<>();
        Map<Integer, String> userIdToNickName = new HashMap<>();
        if(!CollectionUtils.isEmpty(orders)) {
            for(Order order : orders) {
                userIds.add(order.getUserId());
            }
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("userId").in(userIds));
            query1.addCriteria(Criteria.where("isDeleted").is(0));
            List<UserInfo> userInfos = mongoTemplate.find(query1, UserInfo.class);
            if(!CollectionUtils.isEmpty(userInfos)) {
                for(UserInfo user : userInfos) {
                    userIdToNickName.put(user.getUserId(), user.getNickName());
                }
            }

            for(Order order : orders) {
                Map<String, Object> map = JavaBeanUtils.convertBeanToMap(order);
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                map.put("gmtCreate", format.format(order.getGmtCreate()));
                map.put("nickName", userIdToNickName.get(order.getUserId()));
                orderVOS.add(map);
            }
        }
        return orderVOS;
    }

    public Order orderDelivery(String orderId, String logistics, String logisticsNumber) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
        query.addCriteria(Criteria.where("status").is(1));

        Update update = new Update();
        if(!StringUtils.isEmpty(logisticsNumber)){
            update.set("logisticsNumber", logisticsNumber);
        }
        update.set("companyName", logistics);
        update.set("status", 2);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        Order newOrder = mongoTemplate.findAndModify(query, update, options, Order.class);
        if(newOrder == null) {
            throw new MessageException(StringConst.ERRCODE_X, "订单修改失败");
        }

        return newOrder;
    }

    public Order changeOrderStatus(String orderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
        query.addCriteria(Criteria.where("status").is(0));
        Update update = new Update();
        update.set("status", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(true);
        Order newOrder = mongoTemplate.findAndModify(query, update, options, Order.class);

        return newOrder;
    }

    public Map<String, String> createDoc(Order order, UserInfo user){
        Map<String, String> dataMap = new HashMap<>();
        if(StringUtils.isEmpty(user.getNickName())) {
            dataMap.put("nickName", "");
        }else{
            dataMap.put("nickName", user.getNickName());
        }

        dataMap.put("orderserializable", order.getOrderserializable());
        if(StringUtils.isEmpty(order.getClientName())) {
            dataMap.put("clientName", "");
        }else{
            dataMap.put("clientName", order.getClientName());
        }

        if(StringUtils.isEmpty(order.getClientPhone())) {
            dataMap.put("clientPhone", "");
        }else{
            dataMap.put("clientPhone", order.getClientPhone());
        }

        if(StringUtils.isEmpty(order.getAddress())) {
            dataMap.put("address", "");
        }else{
            dataMap.put("address", order.getAddress());
        }

        if(StringUtils.isEmpty(order.getCompanyName())) {
            dataMap.put("companyName", "");
        }else{
            dataMap.put("companyName", order.getCompanyName());
        }

        if(StringUtils.isEmpty(order.getLogisticsNumber())) {
            dataMap.put("logisticsNumber", "");
        }else{
            dataMap.put("logisticsNumber", order.getLogisticsNumber());
        }

        String goodsName = "";
        for(OrderGoods orderGoods : order.getOrderGoodss()) {
            goodsName = goodsName.concat(orderGoods.getModel()).concat("-").concat(orderGoods.getColor()).concat("-").concat(orderGoods.getMaterial()).
                    concat(" * ").concat("" + orderGoods.getNumber() + ",");
        }
        dataMap.put("goodsName", goodsName);

        if(StringUtils.isEmpty(order.getRemark())) {
            dataMap.put("remark", "");
        }else{
            dataMap.put("remark", order.getRemark());
        }

        return dataMap;
    }
}

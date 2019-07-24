package com.baishiyuan.component;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.domain.*;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.JavaBeanUtils;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.Utils;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.dozer.Mapper;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
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
import java.math.BigInteger;
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

    public XWPFDocument createDoc1(Order order, UserInfo user){
        //Blank Document
        XWPFDocument document= new XWPFDocument();

        //添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        //设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText("百世缘家具（出货单）");
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(16);


//        //段落
//        XWPFParagraph firstParagraph = document.createParagraph();
//        XWPFRun run = firstParagraph.createRun();
//        run.setText("详细信息");
//        run.setColor("696969");
//        run.setFontSize(16);
//
//        //设置段落背景颜色
//        CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
//        cTShd.setVal(STShd.CLEAR);
//        cTShd.setFill("97FFFF");


//        //换行
//        XWPFParagraph paragraph1 = document.createParagraph();
//        XWPFRun paragraphRun1 = paragraph1.createRun();
//        paragraphRun1.setText("\r");

        //第一行
        XWPFParagraph paragraph2 = document.createParagraph();
        XWPFRun paragraphRun2 = paragraph2.createRun();
        paragraphRun2.setText("客户姓名: " + user.getNickName() + "                   订单号: " + order.getOrderserializable());

        //基本信息表格
        XWPFTable infoTable = document.createTable();
        //去表格边框
        infoTable.getCTTbl().getTblPr().unsetTblBorders();


        //列宽自动分割
        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
        infoTableWidth.setType(STTblWidth.DXA);
        infoTableWidth.setW(BigInteger.valueOf(9072));


        //表格第一行
        XWPFTableRow infoTableRowOne = infoTable.getRow(0);
        infoTableRowOne.createCell();
        infoTableRowOne.getCell(0).setText("收货人");
        if(StringUtils.isEmpty(order.getClientName())) {
            infoTableRowOne.addNewTableCell().setText("");
        }else{
            infoTableRowOne.addNewTableCell().setText(order.getClientName());
        }

        infoTableRowOne.addNewTableCell().setText("电话");
        if(StringUtils.isEmpty(order.getClientPhone())) {
            infoTableRowOne.addNewTableCell().setText("");
        }else{
            infoTableRowOne.addNewTableCell().setText(order.getClientPhone());
        }

        //表格第二行
        XWPFTableRow infoTableRowTwo = infoTable.createRow();
        infoTableRowTwo.getCell(0).setText("收件人地址");
        if(StringUtils.isEmpty(order.getAddress())) {
            infoTableRowTwo.getCell(1).setText("");
        }else{
            infoTableRowTwo.getCell(1).setText(order.getAddress());
        }

        //表格第三行
        XWPFTableRow infoTableRowThree = infoTable.createRow();
        infoTableRowThree.getCell(0).setText("发货物流");
        if(StringUtils.isEmpty(order.getCompanyName())) {
            infoTableRowThree.getCell(1).setText("");
        }else{
            infoTableRowThree.getCell(1).setText(order.getCompanyName());
        }
        infoTableRowThree.getCell(2).setText("单号");
        if(StringUtils.isEmpty(order.getLogisticsNumber())) {
            infoTableRowThree.getCell(3).setText("");
        }else{
            infoTableRowThree.getCell(3).setText(order.getLogisticsNumber());
        }

        String goodsName = "";
        for(OrderGoods orderGoods : order.getOrderGoodss()) {
            goodsName.concat(orderGoods.getModel()).concat("-").concat(orderGoods.getColor()).concat("-").concat(orderGoods.getMaterial()).
                    concat(" * ").concat("" + orderGoods.getNumber() + ",");
        }


        //表格第四行
        XWPFTableRow infoTableRowFour = infoTable.createRow();
        infoTableRowFour.getCell(0).setText("购买物品");
        infoTableRowFour.getCell(1).setText(goodsName);

        //表格第五行
        XWPFTableRow infoTableRowFive = infoTable.createRow();
        infoTableRowFive.getCell(0).setText("其他备注");
        if(StringUtils.isEmpty(order.getRemark())) {
            infoTableRowFive.getCell(1).setText("");
        }else{
            infoTableRowFive.getCell(1).setText(order.getRemark());
        }

        //两个表格之间加个换行
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText("\r");

        //最后一行
        XWPFParagraph paragraph4 = document.createParagraph();
        XWPFRun paragraphRun4 = paragraph4.createRun();
        paragraphRun4.setText("说明：需方收到货物后立刻拆开包装，发现物品有质量问题，需于三天内通知本厂，逾期概不负责。如有疑问请致电 075763210900");

        //换行
        XWPFParagraph paragraph5 = document.createParagraph();
        XWPFRun paragraphRun5 = paragraph5.createRun();
        paragraphRun5.setText("\r");

        //最后一行
        XWPFParagraph paragraph6 = document.createParagraph();
        XWPFRun paragraphRun6 = paragraph6.createRun();
        paragraphRun6.setText("制单人：                         备货人签名：                         提货人/物流签名：                         ");

//        //工作经历表格
//        XWPFTable ComTable = document.createTable();
//
//
//        //列宽自动分割
//        CTTblWidth comTableWidth = ComTable.getCTTbl().addNewTblPr().addNewTblW();
//        comTableWidth.setType(STTblWidth.DXA);
//        comTableWidth.setW(BigInteger.valueOf(9072));

//        //表格第一行
//        XWPFTableRow comTableRowOne = ComTable.getRow(0);
//        comTableRowOne.getCell(0).setText("开始时间");
//        comTableRowOne.addNewTableCell().setText("结束时间");
//        comTableRowOne.addNewTableCell().setText("公司名称");
//        comTableRowOne.addNewTableCell().setText("title");
//
//        //表格第二行
//        XWPFTableRow comTableRowTwo = ComTable.createRow();
//        comTableRowTwo.getCell(0).setText("2016-09-06");
//        comTableRowTwo.getCell(1).setText("至今");
//        comTableRowTwo.getCell(2).setText("seawater");
//        comTableRowTwo.getCell(3).setText("Java开发工程师");
//
//        //表格第三行
//        XWPFTableRow comTableRowThree = ComTable.createRow();
//        comTableRowThree.getCell(0).setText("2016-09-06");
//        comTableRowThree.getCell(1).setText("至今");
//        comTableRowThree.getCell(2).setText("seawater");
//        comTableRowThree.getCell(3).setText("Java开发工程师");


        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

        //添加页眉
        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "Java POI create MS word file.";
        ctHeader.setStringValue(headerText);
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
        //设置为右对齐
        headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
        XWPFParagraph[] parsHeader = new XWPFParagraph[1];
        parsHeader[0] = headerParagraph;
        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);


        //添加页脚
        CTP ctpFooter = CTP.Factory.newInstance();
        CTR ctrFooter = ctpFooter.addNewR();
        CTText ctFooter = ctrFooter.addNewT();
        String footerText = "http://blog.csdn.net/zhouseawater";
        ctFooter.setStringValue(footerText);
        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFParagraph[] parsFooter = new XWPFParagraph[1];
        parsFooter[0] = footerParagraph;
        policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);

        return document;
    }
}

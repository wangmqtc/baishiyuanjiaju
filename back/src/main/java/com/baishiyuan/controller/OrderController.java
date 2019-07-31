package com.baishiyuan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.DTO.OrderQueryDTO;
import com.baishiyuan.component.*;
import com.baishiyuan.domain.*;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@RestController
@RequestMapping("/order")
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

    @Resource
    private ClientBenefitComponent clientBenefitComponent;

    @Resource
    private GoodsMonthStatisticsComponent goodsMonthStatisticsComponent;

    static ExecutorService exec = Executors.newFixedThreadPool(5);

    @RequestMapping(value = "", method = {RequestMethod.POST})
    public WebResult order(@RequestParam String address, @RequestParam String phone, @RequestParam String name, String remark, HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);

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

        //选取modelId和number
        Map<String, Integer> modelToNumberDTOMap = new HashMap<>();

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

            if(modelToNumberDTOMap.containsKey(goods.getModelId())) {
                int number = modelToNumberDTOMap.get(goods.getModelId());
                number += goodsIdToNumber.get(goods.getId());
                modelToNumberDTOMap.put(goods.getModelId(), number);
            }else {
                modelToNumberDTOMap.put(goods.getModelId(), goodsIdToNumber.get(goods.getId()));
            }

            //计算总金钱
            totalPrice += goodsIdToNumber.get(goods.getId())*goods.getPrice();
        }

        //折扣操作
        ClientBenefit clientBenefit = clientBenefitComponent.getClientBenefitByUserId(sessionInfo.getUserId());
        if(clientBenefit != null) {
            if(clientBenefit.getDisCount() != null && clientBenefit.getType() != null && clientBenefit.getType() == 0) {
                totalPrice = new Double(totalPrice * clientBenefit.getDisCount()).intValue();
            }
        }

        Query userAccountQuery = new Query();
        userAccountQuery.addCriteria(Criteria.where("userId").is(sessionInfo.getUserId()));
        userAccountQuery.addCriteria(Criteria.where("totalAssets").gte(totalPrice));
        userAccountQuery.addCriteria(Criteria.where("availableAssets").gte(totalPrice));
        boolean isExist = mongoTemplate.exists(userAccountQuery, UserAccount.class);
        if(!isExist) {
            throw new MessageException(StringConst.ERRCODE_X, "您的余额不够！");
        }

       JSONObject jsonObject = orderComponent.reduceMoney(totalPrice, sessionInfo.getUserId(), name);

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
        order.setCostMoney(totalPrice);
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
        order.setOrderserializable(serialNumber);
        mongoTemplate.insert(order);

        String userFlowId = jsonObject.getString("id");
        if(!StringUtils.isEmpty(userFlowId)) {
            orderComponent.upadateFlowRemark(userFlowId, "购买商品,订单号为:" + order.getOrderserializable());
        }

        //清除购物车
        shoppingCartComponent.deleteGoodsInShoppingCartByUserId(sessionInfo.getUserId(), shoppingCartIds);

        //统计每个月的购物量
        exec.execute(new Task(modelToNumberDTOMap));

        return new WebResult(StringConst.ERRCODE_SUCCESS, "下订单成功", 1);
    }


    public class Task implements Runnable {
        private Map<String, Integer> map;

        public Task(Map<String, Integer> map) {
            this.map = map;
        }

        public void run() {
            Set<String> keySets = map.keySet();
            for(String modelId : keySets) {
                goodsMonthStatisticsComponent.addGoodsMonthStatistics(modelId, map.get(modelId));
            }
        }
    }

    @RequestMapping(value = "/{orderId}", method = {RequestMethod.PUT})
    public WebResult updateOrder(@PathVariable String orderId, @RequestParam String logistics, @RequestParam String logisticsNumber, String remark,
                                  HttpServletRequest request) {
        SessionInfo sessionInfo = checkOrderAuth(request);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(orderId));
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
        SessionInfo sessionInfo = getSession(request);

        Page page = orderComponent.queryOrders(orderQueryDTO.getPageNo(), orderQueryDTO.getPageSize(), orderQueryDTO.getStatus());
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @RequestMapping(value = "/orderDelivery/{orderId}", method = {RequestMethod.PUT})
    public WebResult orderDelivery(@PathVariable String orderId, @RequestParam String logistics, @RequestParam String logisticsNumber, HttpServletRequest request) {
        SessionInfo sessionInfo = checkOrderAuth(request);

        Order newOrder = orderComponent.orderDelivery(orderId, logistics, logisticsNumber);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "已更新状态", newOrder);
    }

    @RequestMapping(value = "/orderPrint", method = {RequestMethod.GET})
    public WebResult orderPrint(@RequestParam String orderId, HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);
        orderComponent.updatePrintNumber(orderId);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "打印次数更新", 1);
    }

    @RequestMapping(value = "/{orderId}", method = {RequestMethod.GET})
    public WebResult querySingleOrder(@PathVariable String orderId, HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);

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

    @CrossOrigin
    @RequestMapping(value = "/orderExort/{orderId}", method = {RequestMethod.GET})
    public void orderExort(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = getSession(request);

        int userId = sessionInfo.getUserId();
        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(userId);
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此用户");
        }

        Order order = orderComponent.getSingleOrderById(orderId);
        if(order == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此订单");
        }

        Map<String, String> dataMap = orderComponent.createDoc(order, userInfo);

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

    public void orderExortPDF1(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        SessionInfo sessionInfo = getSession(request);

        HttpHeaders headers = new HttpHeaders();

        int userId = sessionInfo.getUserId();
        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(userId);
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此用户");
        }

        Order order = orderComponent.getSingleOrderById(orderId);
        if(order == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此订单");
        }

        Map<String, String> dataMap = orderComponent.createDoc(order, userInfo);

        String fileName = "order_" + orderId + ".pdf";

        try {
            byte[] bytes = fileName.getBytes("gb2312");
            fileName = new String(bytes, "ISO8859-1");
            response.setContentType("application/force-download");
            response.setContentType("application/download");
            response.setContentType("application/octet-stream");

            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

            //创建配置实例
            Configuration configuration = new Configuration();
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            //设置编码
            configuration.setDefaultEncoding("UTF-8");
            //ftl模板文件
            configuration.setClassForTemplateLoading(WordUtil.class,"/");
            //获取模板
            Template template = configuration.getTemplate("11.ftl");
            //将模板和数据模型合并生成文件
            StringWriter str = new StringWriter();
            //生成文件
            template.process(dataMap, str);
            str.flush();
            String htmlTmpStr = str.toString();
            //关闭流
            str.flush();
            str.close();

            /** -------生成PDF------- **/
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlTmpStr);
            // 解决中文支持问题
            ITextFontResolver fontResolver = renderer.getFontResolver();
            String simusun = "./simsun.ttc";
            if (simusun != null) {
                fontResolver.addFont(simusun, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                renderer.getSharedContext().setFontResolver(fontResolver);
            }
            renderer.layout();
            renderer.createPDF(response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


    public void orderExortPDF(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        Order order = orderComponent.getSingleOrderById(orderId);
        if(order == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此订单");
        }

        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(order.getUserId());
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此用户");
        }

        Map<String, String> dataMap = orderComponent.createDoc(order, userInfo);

        String fileName = "order_" + orderId + ".pdf";

        // 模板路径
        String templatePath = "./order_pdf.pdf";
        PdfReader reader;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            response.setContentType("application/pdf");

            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();

            form.setField("nickName",dataMap.get("nickName"));
            form.setField("orderserializable",dataMap.get("orderserializable"));
            form.setField("clientName",dataMap.get("clientName"));
            form.setField("clientPhone",dataMap.get("clientPhone"));
            form.setField("address",dataMap.get("address"));
            form.setField("companyName",dataMap.get("companyName"));
            form.setField("logisticsNumber",dataMap.get("logisticsNumber"));
            form.setField("goodsName",dataMap.get("goodsName"));
            form.setField("remark",dataMap.get("remark"));

            stamper.setFormFlattening(true);// 如果为false那么生成的PDF文件还能编辑，一定要设为true
            stamper.close();

            response.setBufferSize(10028000);
            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, response.getOutputStream());
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();

            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(value = "/orderExortPDF/{orderId}", method = {RequestMethod.GET})
    public void orderExortPDF2(@PathVariable String orderId, HttpServletRequest request, HttpServletResponse response) {
        Order order = orderComponent.getSingleOrderById(orderId);
        if(order == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此订单");
        }

        UserInfo userInfo = userInfoComponent.getUserInfoByUserId(order.getUserId());
        if(userInfo == null) {
            throw new MessageException(StringConst.ERRCODE_X, "没有此用户");
        }

        Map<String, String> dataMap = orderComponent.createDoc(order, userInfo);

        // 模板路径
        String templatePath = "./order_pdf.pdf";

        PdfReader reader;
        FileOutputStream out;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        try {
            response.setContentType("application/pdf");

            BaseFont bf = BaseFont.createFont("./simsun.ttc,1" , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font FontChinese = new Font(bf, 3f, Font.NORMAL);
            reader = new PdfReader(templatePath);// 读取pdf模板
            bos = new ByteArrayOutputStream();
            stamper = new PdfStamper(reader, bos);
            AcroFields form = stamper.getAcroFields();
            //文字类的内容处理
            form.addSubstitutionFont(bf);
            PdfContentByte over = stamper.getOverContent(0);
            for(String key : dataMap.keySet()){
                String value = dataMap.get(key);
                form.setFieldProperty(key,"textfont",3,null);
                form.setField(key,value);
            }
            stamper.setFormFlattening(true);// 如果为false，生成的PDF文件可以编辑，如果为true，生成的PDF文件不可以编辑
            stamper.close();

            Document doc = new Document();
            PdfCopy copy = new PdfCopy(doc, response.getOutputStream());
            doc.open();
            PdfImportedPage importPage = copy.getImportedPage(new PdfReader(bos.toByteArray()), 1);
            copy.addPage(importPage);
            doc.close();

            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (Exception e) {
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

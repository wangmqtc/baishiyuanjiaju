package com.baishiyuan.controller;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.DTO.GoodsCreateDTO;
import com.baishiyuan.DTO.GoodsDTO;
import com.baishiyuan.component.GoodsComponent;
import com.baishiyuan.component.GoodsMonthStatisticsComponent;
import com.baishiyuan.domain.Goods;
import com.baishiyuan.domain.GoodsModel;
import com.baishiyuan.domain.SessionInfo;
import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.*;
import com.baishiyuan.vo.GoodsVO;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    protected Mapper dozerMapper;

    @Resource
    private GoodsComponent goodsComponent;

    @Resource
    private GoodsMonthStatisticsComponent goodsMonthStatisticsComponent;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public WebResult createGoods(@RequestBody @Validated GoodsCreateDTO goodsCreateDTO, HttpServletRequest request) {
        SessionInfo sessionInfo = checkGoodsAuth(request);

        List<GoodsDTO> goodsDTOS = goodsCreateDTO.getGoodsDTOS();

        /**先判断是否有重复的产品型号*/
        Query query = new Query();
        query.addCriteria(Criteria.where("publishName").is(goodsCreateDTO.getPublishName()));
        if(mongoTemplate.exists(query, GoodsModel.class)) {
            throw new MessageException(ErrorInfo.ERROR, "已经有重复的系列名");
        }

        Date now = Calendar.getInstance().getTime();
        GoodsModel goodsModel = new GoodsModel();
        goodsModel.setModel(goodsCreateDTO.getModel());
        goodsModel.setDescription(goodsCreateDTO.getDetails());
        goodsModel.setCreator(sessionInfo.getUserId());
        goodsModel.setGmtCreate(now);
        goodsModel.setNumber(goodsDTOS.size());
        goodsModel.setPublishName(goodsCreateDTO.getPublishName());

        mongoTemplate.insert(goodsModel);

        List<Goods> goodss = goodsComponent.goodsDTOSToGoods(goodsCreateDTO, goodsModel.getId(), sessionInfo.getUserId(), now);
        /**初始化产品名字*/
        for(Goods goods : goodss) {
            goods.setName(goods.getModel().concat("_").concat(goods.getColor()).concat("_").concat(goods.getMaterial()));
        }
        mongoTemplate.insertAll(goodss);

        logger.info("ok");

        /**随机抽取一条产品id给model*/
        goodsModel.setGoodsIdRandom(goodss.get(0).getId());
        mongoTemplate.save(goodsModel);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "发布成功", goodss);
    }

    @RequestMapping(value = "/{modelId}", method = RequestMethod.PUT, produces = "application/json")
    public WebResult updateGoods(@RequestBody @Validated GoodsCreateDTO goodsCreateDTO, @PathVariable String modelId, HttpServletRequest request) {
        SessionInfo sessionInfo = checkGoodsAuth(request);

        Date now = Calendar.getInstance().getTime();

        /**先判断是否有重复的产品型号*/
        Query query = new Query();
        query.addCriteria(Criteria.where("publishName").is(goodsCreateDTO.getPublishName()));
        query.addCriteria(Criteria.where("id").ne(modelId));
        if(mongoTemplate.exists(query, GoodsModel.class)) {
            throw new MessageException(ErrorInfo.ERROR, "已经有重复的产品号");
        }

        List<GoodsDTO> goodsDTOS = goodsCreateDTO.getGoodsDTOS();
        query = new Query();
        query.addCriteria(Criteria.where("id").is(modelId));
        Update update = new Update();
        update.set("number", goodsDTOS.size());
        update.set("model", goodsCreateDTO.getModel());
        update.set("description", goodsCreateDTO.getDetails());
        update.set("gmtModified", now);
        update.set("modifier", sessionInfo.getUserId());
        update.set("publishName", goodsCreateDTO.getPublishName());
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(false);
        options.returnNew(false);
        GoodsModel goodsModel = mongoTemplate.findAndModify(query, update, options, GoodsModel.class);
        if(goodsModel == null) {
            throw new MessageException(ErrorInfo.ERROR, "没有查到该型号");
        }

        List<Goods> goodss = goodsComponent.goodsDTOSToGoods(goodsCreateDTO, modelId, sessionInfo.getUserId(), now);

        /**初始化产品名字*/
        for(Goods goods : goodss) {
            goods.setName(goods.getModel().concat("_").concat(goods.getColor()).concat("_").concat(goods.getMaterial()));
        }

        /**先删除旧数据*/
        Query goodssQuery = new Query();
        goodssQuery.addCriteria(Criteria.where("modelId").is(modelId));
        mongoTemplate.remove(goodssQuery, Goods.class);

        mongoTemplate.insertAll(goodss);

        /**随机抽取一条产品id给model*/
        query = new Query();
        query.addCriteria(Criteria.where("id").is(modelId));
        update = new Update();
        update.set("goodsIdRandom", goodss.get(0).getId());
        mongoTemplate.findAndModify(query, update, options, GoodsModel.class);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "修改成功", goodss);
    }

    @RequestMapping(value = "/{modelid}", method = RequestMethod.DELETE, produces = "application/json")
    public WebResult deleteGoods(@PathVariable String modelid,  HttpServletRequest request) {
        checkGoodsAuth(request);

        goodsComponent.deleteGoodsByModelId(modelid);
        goodsMonthStatisticsComponent.deleteGoodsMonthStatistics(modelid);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "删除成功", 1);
    }

    @RequestMapping(value = "/models", method = RequestMethod.GET)
    public WebResult queryGoodsModels(@RequestParam int pageNo, @RequestParam int pageSize, HttpServletRequest request) {
        getSession(request);

        Page page = goodsComponent.queryGoodsModelsByPage(pageNo, pageSize);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @RequestMapping(value = "/{modelid}", method = RequestMethod.GET)
    public WebResult queryGoodsByModelId(@PathVariable String modelid,  HttpServletRequest request) {
        getSession(request);

        List<GoodsVO> goodsVOS = goodsComponent.queryGoodsByModel(modelid);
        JSONObject result = new JSONObject();
        List<JSONObject> colors = new ArrayList<JSONObject>();
        List<String> materials = new ArrayList<>();

        for(GoodsVO goodsVO : goodsVOS) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("color", goodsVO.getColor());
            jsonObject.put("image", goodsVO.getImage());
            colors.add(jsonObject);
            materials.add(goodsVO.getMaterial());
        }
        result.put("goodss", goodsVOS);
        result.put("colors", colors);
        result.put("materials", materials);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", result);
    }

    @RequestMapping(value = "/homePage", method = RequestMethod.GET)
    public WebResult homePage(@RequestParam int pageNo, @RequestParam int pageSize,  HttpServletRequest request) {
        getSession(request);

        Page page = goodsComponent.queryGoodsByPage(pageNo, pageSize);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", page);
    }

    @RequestMapping(value = "/detail/{modelid}", method = RequestMethod.GET)
    public WebResult goodsDetails(@PathVariable String modelid,  HttpServletRequest request) {
        getSession(request);

        JSONObject jsonObject = goodsComponent.goodsDetailByModel(modelid);
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", jsonObject);
    }


    private SessionInfo checkGoodsAuth(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        /**判断权限*/
        if (!AuthorityUtils.checkGoodsAuth(sessionInfo.getType(), sessionInfo.getAuthority())) {
            throw new MessageException(StringConst.ERRCODE_X, "你没有操作权限！");
        }

        return sessionInfo;
    }

    private SessionInfo testCheckGoodsAuth(HttpServletRequest request) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(1);

        return sessionInfo;
    }

    private SessionInfo getSession(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        return sessionInfo;
    }

}

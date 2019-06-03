package com.baishiyuan.component;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.DTO.GoodsCreateDTO;
import com.baishiyuan.DTO.GoodsDTO;
import com.baishiyuan.domain.Goods;
import com.baishiyuan.domain.GoodsModel;
import com.baishiyuan.utils.Page;
import com.baishiyuan.vo.GoodsVO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@Component("goodsComponent")
public class GoodsComponent {

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    protected Mapper dozerMapper;

    public List<Goods> goodsDTOSToGoods(GoodsCreateDTO goodsCreateDTO, String modelId, int creator, Date date) {
        List<GoodsDTO> goodsDTOS = goodsCreateDTO.getGoodsDTOS();
        List<Goods> goodsList = new ArrayList<>();

        if(CollectionUtils.isEmpty(goodsDTOS)) {
            return goodsList;
        }
        for(GoodsDTO goodsDTO : goodsDTOS) {
            Goods goods = new Goods();
            dozerMapper.map(goodsDTO, goods);
            goods.setDescription(goodsCreateDTO.getDetails());
            goods.setModel(goodsCreateDTO.getModel());
            goods.setPublishName(goodsCreateDTO.getPublishName());
            goods.setModelId(modelId);
            goods.setCreator(creator);
            goods.setGmtCreate(date);
            goodsList.add(goods);
        }
        return goodsList;
    }

    public boolean deleteGoodsByModelId(String modelId) {
        Query goodssQuery = new Query();
        goodssQuery.addCriteria(Criteria.where("modelId").is(modelId));
        mongoTemplate.remove(goodssQuery, Goods.class);

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(modelId));
        mongoTemplate.remove(query, GoodsModel.class);
        return true;
    }

    public Page queryGoodsModelsByPage(int pageNo, int pageSize) {
        Query query = new Query();
        Long totalNum = mongoTemplate.count(query, GoodsModel.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        page.setList(mongoTemplate.find(query, GoodsModel.class));
        return page;
    }

    public List<GoodsVO> queryGoodsByModel(String modelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("modelId").is(modelId));
        List<Goods> goodss = mongoTemplate.find(query, Goods.class);

        List<GoodsVO> goodsVOS = new ArrayList<>();
        if(CollectionUtils.isEmpty(goodss)) {
            return goodsVOS;
        }

        for(Goods goods : goodss) {
            GoodsVO goodsVO = new GoodsVO();
            dozerMapper.map(goods, goodsVO);
            goodsVOS.add(goodsVO);
        }

        return goodsVOS;
    }

    public Page queryGoodsByPage(int pageNo, int pageSize) {
        Query query = new Query();
        Long totalNum = mongoTemplate.count(query, GoodsModel.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        List<GoodsModel> goodsModels = mongoTemplate.find(query, GoodsModel.class);
        List<String> goodsIds = new ArrayList<>();

        if(CollectionUtils.isEmpty(goodsModels)) {
            page.setList(new ArrayList());
            return page;
        }

        for(GoodsModel goodsModel : goodsModels) {
            goodsIds.add(goodsModel.getGoodsIdRandom());
        }
        query = new Query();
        query.addCriteria(Criteria.where("id").in(goodsIds));
        page.setList(mongoTemplate.find(query, Goods.class));
        return page;
    }

    public JSONObject goodsDetailByModel(String modelId) {
        JSONObject jsonObject = new JSONObject();

        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(modelId));
        GoodsModel goodsModel = mongoTemplate.findOne(query, GoodsModel.class);
        if(goodsModel == null) {
            return null;
        }

        jsonObject.put("model", goodsModel.getModel());
        jsonObject.put("description", goodsModel.getDescription());

        query = new Query();
        query.addCriteria(Criteria.where("modelId").is(modelId));
        List<Goods> goodss = mongoTemplate.find(query, Goods.class);
        List<GoodsVO> goodsVOS = new ArrayList<>();

        if(!CollectionUtils.isEmpty(goodss)) {
            for(Goods goods : goodss) {
                GoodsVO goodsVO = new GoodsVO();
                dozerMapper.map(goods, goodsVO);
                goodsVOS.add(goodsVO);
            }
        }
        jsonObject.put("list", goodsVOS);
        return jsonObject;
    }

}

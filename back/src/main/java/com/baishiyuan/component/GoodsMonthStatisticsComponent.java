package com.baishiyuan.component;

import com.baishiyuan.domain.GoodsModel;
import com.baishiyuan.domain.GoodsMonthStatistics;
import com.baishiyuan.domain.Order;
import com.baishiyuan.utils.Page;
import com.baishiyuan.vo.GoodsMonthStatisticsVO;
import com.baishiyuan.vo.GoodsVO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2019/4/21 0021.
 */
@Component("goodsMonthStatisticsComponent")
public class GoodsMonthStatisticsComponent {

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    protected Mapper dozerMapper;

    public void addGoodsMonthStatistics(String modelId, int number) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);    //获取年
        int month = calendar.get(Calendar.MONTH) + 1;   //获取月份，0表示1月份

        Query query = new Query();
        query.addCriteria(Criteria.where("modelId").is(modelId));
        query.addCriteria(Criteria.where("month").is(month));
        query.addCriteria(Criteria.where("year").is(year));
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        Update update = new Update();
        update.inc("number", number);
        mongoTemplate.findAndModify(query, update, options, GoodsMonthStatistics.class);
    }

    public Page queryGoodsMonthStatisticsByPage(int year, int month, int pageNo, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("month").is(month));
        query.addCriteria(Criteria.where("year").is(year));
        Long totalNum = mongoTemplate.count(query, GoodsMonthStatistics.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "number"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);
        List<GoodsMonthStatistics> goodsMonthStatisticss = mongoTemplate.find(query, GoodsMonthStatistics.class);

        List<String> modelIds = new ArrayList<>();
        for(GoodsMonthStatistics goodsMonthStatistic : goodsMonthStatisticss) {
            modelIds.add(goodsMonthStatistic.getModelId());
        }

        query = new Query();
        query.addCriteria(Criteria.where("id").in(modelIds));
        List<GoodsModel> goodsModels = mongoTemplate.find(query, GoodsModel.class);
        Map<String, GoodsModel> map = new HashMap<>();
        for(GoodsModel goodsModel : goodsModels) {
            map.put(goodsModel.getId(), goodsModel);
        }

        List<GoodsMonthStatisticsVO> goodsMonthStatisticsVOS = new ArrayList<>();
        for(GoodsMonthStatistics goodsMonthStatistic : goodsMonthStatisticss) {
            GoodsMonthStatisticsVO goodsMonthStatisticsVO = new GoodsMonthStatisticsVO();
            dozerMapper.map(goodsMonthStatistic, goodsMonthStatisticsVO);
            GoodsModel goodsModel = map.get(goodsMonthStatistic.getModelId());
            if(goodsModel != null) {
                goodsMonthStatisticsVO.setPublishName(goodsModel.getPublishName());
                goodsMonthStatisticsVO.setModel(goodsModel.getModel());
            }
            goodsMonthStatisticsVOS.add(goodsMonthStatisticsVO);
        }

        page.setList(goodsMonthStatisticsVOS);
        return page;
    }

    public void deleteGoodsMonthStatistics(String modelId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("modelId").is(modelId));
        mongoTemplate.findAndRemove(query, GoodsMonthStatistics.class);
    }

}

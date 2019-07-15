package com.baishiyuan.component;

import com.baishiyuan.domain.ClientBenefit;
import com.baishiyuan.domain.Order;
import com.baishiyuan.domain.UserInfo;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.Utils;
import com.baishiyuan.vo.ClientBenefitVO;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Component("clientBenefitComponent")
public class ClientBenefitComponent{

    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;


    public void setDisCount(int userId, int creator, double disCount) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("type").is(0));
        ClientBenefit clientBenefit = mongoTemplate.findOne(query, ClientBenefit.class);
        if(clientBenefit == null){
            clientBenefit = new ClientBenefit();
            clientBenefit.setUserId(userId);
            clientBenefit.setCreator(creator);
            clientBenefit.setGmtCreate(Calendar.getInstance().getTime());
            clientBenefit.setDisCount(disCount);
            clientBenefit.setType(0);
            mongoTemplate.insert(clientBenefit);
        }else {
            Update update = new Update();
            update.set("disCount", disCount);
            update.set("modifier", creator);
            update.set("gmtModified", Calendar.getInstance().getTime());
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("id").is(clientBenefit.getId()));
            mongoTemplate.findAndModify(query1, update, ClientBenefit.class);
        }
    }

    public void setUrgentCount(int userId, int creator, Integer urgentCount) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("type").is(1));
        ClientBenefit clientBenefit = mongoTemplate.findOne(query, ClientBenefit.class);
        if(clientBenefit == null){
            clientBenefit = new ClientBenefit();
            clientBenefit.setUserId(userId);
            clientBenefit.setCreator(creator);
            clientBenefit.setGmtCreate(Calendar.getInstance().getTime());
            clientBenefit.setUrgentCount(urgentCount);
            clientBenefit.setType(1);
            mongoTemplate.insert(clientBenefit);
        }else {
            Update update = new Update();
            update.set("urgentCount", urgentCount);
            update.set("modifier", creator);
            update.set("gmtModified", Calendar.getInstance().getTime());
            Query query1 = new Query();
            query1.addCriteria(Criteria.where("id").is(clientBenefit.getId()));
            mongoTemplate.findAndModify(query1, update, ClientBenefit.class);
        }
    }

    public void cancelBenefit(int type, int userId, int modifier) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("type").is(type));
        mongoTemplate.findAllAndRemove(query, ClientBenefit.class);
    }

    public Page getUserBenefits(int pageNo, int pageSize, int type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(2));

        Long totalNum = mongoTemplate.count(query, UserInfo.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        List<UserInfo> userInfos = mongoTemplate.find(query, UserInfo.class);
        if(CollectionUtils.isEmpty(userInfos)){
            page.setList(new ArrayList());
            return page;
        }

        List<Integer> list = new ArrayList<>();
        for(UserInfo userInfo : userInfos){
            list.add(userInfo.getUserId());
        }
        Map<Integer, ClientBenefit> result = getBenefits(list, type);

        List<ClientBenefitVO> clientBenefitVOS = new ArrayList<>();
        for(UserInfo userInfo : userInfos){
            ClientBenefitVO clientBenefitVO = new ClientBenefitVO();
            clientBenefitVO.setType(type);
            clientBenefitVO.setUserId(userInfo.getUserId());
            clientBenefitVO.setNickName(userInfo.getNickName());
            clientBenefitVO.setMbn(userInfo.getMbn());
            clientBenefitVO.setRealName(userInfo.getRealName());
            ClientBenefit clientBenefit = result.get(userInfo.getUserId());
            if(clientBenefit == null){
                clientBenefitVO.setStatus(0);
            }else{
                clientBenefitVO.setStatus(1);
                if(type == 0){
                    clientBenefitVO.setDisCount(clientBenefit.getDisCount());
                }else if(type == 1){
                    clientBenefitVO.setUrgentCount(clientBenefit.getUrgentCount());
                }
            }

            clientBenefitVOS.add(clientBenefitVO);
        }
        page.setList(clientBenefitVOS);
        return page;
    }

    public Integer checkRemainingUrgentCount(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("type").is(1));
        ClientBenefit clientBenefit = mongoTemplate.findOne(query, ClientBenefit.class);
        if(clientBenefit == null){
            return 0;
        }

        Query dayNowUrgentCount = new Query();
        dayNowUrgentCount.addCriteria(Criteria.where("userId").is(userId));
        dayNowUrgentCount.addCriteria(Criteria.where("createDay").is(Utils.formatIntDay(Calendar.getInstance())));
        dayNowUrgentCount.addCriteria(Criteria.where("status").ne(3));
        dayNowUrgentCount.addCriteria(Criteria.where("isUrgent").is(1));
        Long count = mongoTemplate.count(dayNowUrgentCount, Order.class);

        if(clientBenefit.getUrgentCount() == null){
            throw new MessageException(StringConst.ERRCODE_X, "该用户的加急次数出错！");
        }
        int remaining = clientBenefit.getUrgentCount() - count.intValue();
        if(remaining <= 0){
            remaining = 0;
        }
        return remaining;
    }

    private Map<Integer, ClientBenefit> getBenefits(List<Integer> userIds, int type){
        Map<Integer, ClientBenefit> result = new HashMap<>();

        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type));
        query.addCriteria(Criteria.where("userId").in(userIds));
        List<ClientBenefit> clientBenefits = mongoTemplate.find(query, ClientBenefit.class);
        if(CollectionUtils.isEmpty(clientBenefits)){
            return result;
        }

        for(ClientBenefit clientBenefit : clientBenefits){
            result.put(clientBenefit.getUserId(), clientBenefit);
        }
        return result;
    }

    public ClientBenefit getClientBenefitByUserId(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("type").is(0));
        ClientBenefit clientBenefit = mongoTemplate.findOne(query, ClientBenefit.class);
        return clientBenefit;
    }

}

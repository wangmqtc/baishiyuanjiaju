package com.baishiyuan.component;

import com.baishiyuan.domain.UserAccountFlow;
import com.baishiyuan.utils.Page;
import com.baishiyuan.vo.FlowVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/7/24 0024.
 */
@Component("flowComponent")
public class FlowComponent {
    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;

    public Page queryFlowByPage(Integer userId, int pageNo, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        if(userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        Long totalNum = mongoTemplate.count(query, UserAccountFlow.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        List<UserAccountFlow> userAccountFlows = mongoTemplate.find(query, UserAccountFlow.class);

        List<FlowVO> flowVOS = UserAccountFlowToFlowVOs(userAccountFlows);

        page.setList(flowVOS);
        return page;
    }

    public List<FlowVO> queryFlows(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        if(userId != null) {
            query.addCriteria(Criteria.where("userId").is(userId));
        }
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));

        List<UserAccountFlow> userAccountFlows = mongoTemplate.find(query, UserAccountFlow.class);

        List<FlowVO> flowVOS = UserAccountFlowToFlowVOs(userAccountFlows);
        return flowVOS;
    }

    private List<FlowVO> UserAccountFlowToFlowVOs(List<UserAccountFlow> userAccountFlows) {
        List<FlowVO> flowVOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(userAccountFlows)) {
            for(UserAccountFlow userAccountFlow : userAccountFlows){
                FlowVO flowVO = new FlowVO();
                flowVO.setUserId(userAccountFlow.getUserId());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                flowVO.setGmtCreate(sdf.format(userAccountFlow.getGmtCreate()));
                flowVO.setReason(userAccountFlow.getReason());
                if(userAccountFlow.getOperation() == 1) {
                    double changeMoney = new Double(userAccountFlow.getChangeMoney())/100;
                    flowVO.setChangeMoney(changeMoney * -1);
                }else{
                    double changeMoney = new Double(userAccountFlow.getChangeMoney())/100;
                    flowVO.setChangeMoney(changeMoney);
                }
                if(!StringUtils.isEmpty(userAccountFlow.getClinetName())) {
                    flowVO.setClientName(userAccountFlow.getClinetName());
                }
                flowVOS.add(flowVO);
            }
        }
        return flowVOS;
    }

}

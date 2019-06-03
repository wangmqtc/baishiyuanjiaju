package com.baishiyuan.component;

import com.alibaba.fastjson.JSONObject;
import com.baishiyuan.domain.UserAccount;
import com.baishiyuan.domain.UserAccountFlow;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.Utils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("userAccountComponent")
public class UserAccountComponent{

    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;


    public Page queryUserAccount(int pageNo, int pageSize, Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));

        if(userId != null){
            query.addCriteria(Criteria.where("userId").is(userId));
        }

        Long totalNum = mongoTemplate.count(query, UserAccount.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "totalAssets"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        page.setList(mongoTemplate.find(query, UserAccount.class));
        return page;
    }

    public void createPersonAccount(int userId, int creator) {
        UserAccount userAccount = new UserAccount();
        Calendar calendar = Calendar.getInstance();
        userAccount.setGmtCreate(calendar.getTime());
        userAccount.setUserId(userId);
        userAccount.setIsDeleted(0);
        userAccount.setAvailableAssets(0);
        userAccount.setFrozenAssets(0);
        userAccount.setTotalAssets(0);
        userAccount.setCreator(creator);

        mongoTemplate.insert(userAccount);
    }

    public boolean isExists(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("isDeleted").is(0));
        boolean isExist = mongoTemplate.exists(query, UserAccount.class);
        return isExist;
    }

    public Page queryUserAccounts(int pageNo, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));

        Long totalNum = mongoTemplate.count(query, UserAccount.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "totalAssets"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        page.setList(mongoTemplate.find(query, UserAccount.class));
        return page;
    }

    public void addMoney(int creator, int userId, Integer money, String reason, int type, String eventId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        query.addCriteria(Criteria.where("userId").is(userId));
        UserAccount userAccount = mongoTemplate.findOne(query, UserAccount.class);
        if(userAccount == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户的账户");
        }
        Integer prevMoney = userAccount.getTotalAssets();

        Calendar calendar = Calendar.getInstance();
        Update update = new Update();
        update.inc("availableAssets", money);
        update.inc("totalAssets", money);
        update.set("modifier", creator);
        update.set("gmtModified", calendar.getTime());
        userAccount = mongoTemplate.findAndModify(query, update, UserAccount.class);

        UserAccountFlow userAccountFlow = new UserAccountFlow();

        userAccountFlow.setGmtCreate(calendar.getTime());
        userAccountFlow.setUserId(userId);
        userAccountFlow.setIsDeleted(0);
        userAccountFlow.setCreaDay(Utils.formatIntDay(calendar));
        userAccountFlow.setChangeMoney(money);
        userAccountFlow.setPrevMoney(prevMoney);
        userAccountFlow.setCurrentMoney(userAccount.getTotalAssets() + money);
        userAccountFlow.setChangeReasonType(type);
        userAccountFlow.setEventId(eventId);
        userAccountFlow.setOperation(0);
        userAccountFlow.setReason(reason);
        userAccountFlow.setCreator(creator);
        mongoTemplate.insert(userAccountFlow);
    }

    public void substractMoney(int creator, int userId, Integer money, String reason, int type, String eventId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        query.addCriteria(Criteria.where("userId").is(userId));
        UserAccount userAccount = mongoTemplate.findOne(query, UserAccount.class);
        if(userAccount == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户的账户");
        }
        Integer prevMoney = userAccount.getTotalAssets();
        if(prevMoney < money){
            throw new MessageException(StringConst.ERRCODE_X, "余额不足");
        }

        Calendar calendar = Calendar.getInstance();
        Update update = new Update();

        update.inc("availableAssets", money*-1);
        update.inc("totalAssets", money*-1);
        update.set("modifier", creator);
        update.set("gmtModified", calendar.getTime());

        query.addCriteria(Criteria.where("availableAssets").gte(money));
        query.addCriteria(Criteria.where("totalAssets").gte(money));
        userAccount = mongoTemplate.findAndModify(query, update, UserAccount.class);
        if(userAccount == null){
            throw new MessageException(StringConst.ERRCODE_X, "余额不足");
        }

        UserAccountFlow userAccountFlow = new UserAccountFlow();

        userAccountFlow.setGmtCreate(calendar.getTime());
        userAccountFlow.setUserId(userId);
        userAccountFlow.setIsDeleted(0);
        userAccountFlow.setCreaDay(Utils.formatIntDay(calendar));
        userAccountFlow.setChangeMoney(money);
        userAccountFlow.setPrevMoney(prevMoney);
        userAccountFlow.setCurrentMoney(userAccount.getTotalAssets() - money);
        userAccountFlow.setChangeReasonType(type);
        userAccountFlow.setEventId(eventId);
        userAccountFlow.setOperation(1);
        userAccountFlow.setReason(reason);
        userAccountFlow.setCreator(creator);
        mongoTemplate.insert(userAccountFlow);
    }

    public UserAccount queryUserAccountByUserId(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        query.addCriteria(Criteria.where("userId").is(userId));

        UserAccount userAccount = mongoTemplate.findOne(query, UserAccount.class);

        return userAccount;
    }

    public Page queryUserAccountFlowByPageByUserId(int userId, int pageNo, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        query.addCriteria(Criteria.where("userId").is(userId));

        Long totalNum = mongoTemplate.count(query, UserAccountFlow.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        page.setList(mongoTemplate.find(query, UserAccountFlow.class));
        return page;
    }

    public Page queryAllFlow(int operation , int pageNo, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        if(operation == 0){
            query.addCriteria(Criteria.where("changeReasonType").is(0));
        }else{
            query.addCriteria(Criteria.where("changeReasonType").in(1,3));
        }


        Long totalNum = mongoTemplate.count(query, UserAccountFlow.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        page.setList(mongoTemplate.find(query, UserAccountFlow.class));
        return page;
    }

    public Integer getAllNumber(int operation) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        if(operation == 0){
            query.addCriteria(Criteria.where("changeReasonType").is(0));
        }else{
            query.addCriteria(Criteria.where("changeReasonType").in(1,3));
        }

        List<UserAccountFlow> userAccountFlows =  mongoTemplate.find(query, UserAccountFlow.class);
        if(CollectionUtils.isEmpty(userAccountFlows)){
            return 0;
        }

        Integer allMoney = 0;
        for(UserAccountFlow userAccountFlow : userAccountFlows){
            if(userAccountFlow.getChangeMoney() != null){
                allMoney += userAccountFlow.getChangeMoney();
            }
        }
        return allMoney;
    }

    public Integer getEarliestDay() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        query.with(new Sort(Sort.Direction.ASC, "creaDay"));
        query.limit(1);
        List<UserAccountFlow> userAccountFlows = mongoTemplate.find(query, UserAccountFlow.class);
        if(CollectionUtils.isEmpty(userAccountFlows)){
            return null;
        }
        UserAccountFlow userAccountFlow = userAccountFlows.get(0);
        if(userAccountFlow.getCreaDay() == null){
            throw new MessageException(StringConst.ERRCODE_X, "流水没有日期");
        }
        return userAccountFlow.getCreaDay();
    }

    public Page getFlowsByMonth(int pageNo, int pageSize) {
        Integer day = getEarliestDay();
        if(day == null){
            Page page = new Page(0, pageNo, pageSize);
            page.setList(new ArrayList());
            return  page;
        }

        Page page = new Page(getMonthTotalNumber(day)+1, pageNo, pageSize);

        day = day/100;
        day = day*100 + 1;
        int skipMonths = (pageNo - 1)*pageSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = null;
        try {
            now = sdf.parse(day+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MONTH, skipMonths);


        int startMonth = Utils.formatIntDay(calendar);

        int month = startMonth;
        List<JSONObject> list = new ArrayList<>();
        for(int i = 0; i<pageSize; i++){
            boolean isGtNow = isGtNowByMonth(month);
            if(isGtNow){
                break;
            }

            int startDay = (month/100)*100 + 1;
            int endDay = (month/100)*100 + 32;
            Query query = new Query();
            query.addCriteria(Criteria.where("isDeleted").is(0));
            query.addCriteria(Criteria.where("creaDay").gte(startDay).lt(endDay));
            List<UserAccountFlow> userAccountFlows = mongoTemplate.find(query, UserAccountFlow.class);

            int allMoney = 0;
            for(UserAccountFlow userAccountFlow : userAccountFlows){
                if(userAccountFlow.getOperation() == null || userAccountFlow.getChangeMoney() == null){
                    continue;
                }
                if(userAccountFlow.getChangeReasonType() == 0){
                    allMoney += userAccountFlow.getChangeMoney();
                }else if(userAccountFlow.getChangeReasonType() == 1 || userAccountFlow.getChangeReasonType() == 3){
                    allMoney -= userAccountFlow.getChangeMoney();
                }
            }
            double surplus = new Double(allMoney)/100;
            int nowMoth = month/100;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("month", nowMoth);
            jsonObject.put("surplus", surplus);

            Date now1 = null;
            try {
                now1 = sdf.parse(startDay+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(now1);
            calendar1.add(Calendar.MONTH, 1);
            month = Utils.formatIntDay(calendar1);
            list.add(jsonObject);
        }
        page.setList(list);
        return page;
    }



    public Page getFlowsByYear(int pageNo, int pageSize) {
        Integer day = getEarliestDay();
        if(day == null){
            Page page = new Page(0, pageNo, pageSize);
            page.setList(new ArrayList());
            return  page;
        }

        Page page = new Page(getYearTotalNumber(day)+1, pageNo, pageSize);

        day = day/100;
        day = day*100 + 1;
        int skipYears = (pageNo - 1)*pageSize;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date now = null;
        try {
            now = sdf.parse(day+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, skipYears);


        int startYear = Utils.formatIntDay(calendar);

        int year = startYear;
        List<JSONObject> list = new ArrayList<>();
        for(int i = 0; i<pageSize; i++){
            boolean isGtNow = isGtNowByYear(year);
            if(isGtNow){
                break;
            }

            int startDay = (year/10000)*10000 + 101;
            int endDay = (year/10000)*10000 + 1232;
            Query query = new Query();
            query.addCriteria(Criteria.where("isDeleted").is(0));
            query.addCriteria(Criteria.where("creaDay").gte(startDay).lt(endDay));
            List<UserAccountFlow> userAccountFlows = mongoTemplate.find(query, UserAccountFlow.class);

            int allMoney = 0;
            for(UserAccountFlow userAccountFlow : userAccountFlows){
                if(userAccountFlow.getOperation() == null || userAccountFlow.getChangeMoney() == null){
                    continue;
                }
                if(userAccountFlow.getChangeReasonType() == 0){
                    allMoney += userAccountFlow.getChangeMoney();
                }else if(userAccountFlow.getChangeReasonType() == 1 || userAccountFlow.getChangeReasonType() == 3){
                    allMoney -= userAccountFlow.getChangeMoney();
                }
            }
            double surplus = new Double(allMoney)/100;
            int nowYear = year/10000;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("year", nowYear);
            jsonObject.put("surplus", surplus);

            Date now1 = null;
            try {
                now1 = sdf.parse(startDay+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(now1);
            calendar1.add(Calendar.YEAR, 1);
            year = Utils.formatIntDay(calendar1);
            list.add(jsonObject);
        }
        page.setList(list);
        return page;
    }

    private Integer getMonthTotalNumber(int startMonth){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date startDay = null;
        try {
            startDay = sdf.parse(startMonth+"");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c2.setTime(startDay);

        int result = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        int years = getYearTotalNumber(startMonth);
        result += years*12;
        return result;
    }

    private Integer getYearTotalNumber(int startYear){
        Calendar c1 = Calendar.getInstance();

        int now = Utils.formatIntDay(c1);
        now = now/10000;
        startYear = startYear/10000;

        int result = now - startYear;
        return result;
    }

    private boolean isGtNowByMonth(int day){
        int month = day/100;
        Calendar c1 = Calendar.getInstance();
        int nowMonth = Utils.formatIntDay(c1);
        nowMonth = nowMonth/100;
        if(month > nowMonth){
            return true;
        }
        return false;
    }

    private boolean isGtNowByYear(int day){
        int year = day/10000;
        Calendar c1 = Calendar.getInstance();
        int nowYear = Utils.formatIntDay(c1);
        nowYear = nowYear/10000;
        if(year > nowYear){
            return true;
        }
        return false;
    }
}

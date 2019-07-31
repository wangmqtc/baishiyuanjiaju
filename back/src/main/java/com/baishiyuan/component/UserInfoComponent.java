package com.baishiyuan.component;


import com.baishiyuan.domain.UserInfo;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.Page;
import com.baishiyuan.utils.StringConst;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

@Component("userInfoComponent")
public class UserInfoComponent{

    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;

    public UserInfo getUserInfoByNickName(String nickName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nickName").is(nickName));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);

        return userInfo;
    }

    public UserInfo getUserInfoByUserId(Integer userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);

        return userInfo;
    }

    public Integer getUserMaxId() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "userId"));
        query.limit(1);

        List<UserInfo> userInfos = mongoTemplate.find(query, UserInfo.class);
        if(!CollectionUtils.isEmpty(userInfos)){
            UserInfo userInfo = userInfos.get(0);
            if(userInfo.getUserId() == null){
                return null;
            }
            return userInfo.getUserId();
        }
        return null;
    }

    public boolean addUserInfo(UserInfo userInfo) {
        mongoTemplate.insert(userInfo);
        return true;
    }

    public UserInfo updatePwd(Integer userId, String pwd) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        Update update = new Update();
        update.set("passwd", pwd);

        UserInfo userInfo = mongoTemplate.findAndModify(query, update, UserInfo.class);

        return userInfo;
    }

    public UserInfo fronzeUser(int userId, int type) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        Update update = new Update();
        update.set("isFrozen", type);

        UserInfo userInfo = mongoTemplate.findAndModify(query, update, UserInfo.class);

        return userInfo;
    }

    public boolean existNickName(String nickName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("nickName").is(nickName));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        Boolean result = mongoTemplate.exists(query, UserInfo.class);

        return result;
    }

    public boolean updateUserInfo(UserInfo userInfo) {
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("userId").ne(userInfo.getUserId()));
        query1.addCriteria(Criteria.where("nickName").is(userInfo.getNickName()));
        Boolean isExist = mongoTemplate.exists(query1, UserInfo.class);
        if(isExist){
            throw new MessageException(StringConst.ERRCODE_X, "已存在该昵称，请换一个");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userInfo.getUserId()));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        Update update = new Update();
        update.set("nickName", userInfo.getNickName());
        if(!StringUtils.isEmpty(userInfo.getRealName())){
            update.set("realName", userInfo.getRealName());
        }

        update.set("mbn", userInfo.getMbn());
        update.set("type", userInfo.getType());
        if(userInfo.getAuthority() != null){
            update.set("authority", userInfo.getAuthority());
        }
        update.set("gmtModified", Calendar.getInstance().getTime());

        UserInfo userInfo1 = mongoTemplate.findAndModify(query, update, UserInfo.class);

        if(userInfo1 == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }

        return true;
    }

    public boolean updateUserInfoByMyself(UserInfo userInfo) {
        Query query1 = new Query();
        query1.addCriteria(Criteria.where("userId").ne(userInfo.getUserId()));
        query1.addCriteria(Criteria.where("nickName").is(userInfo.getNickName()));
        Boolean isExist = mongoTemplate.exists(query1, UserInfo.class);
        if(isExist){
            throw new MessageException(StringConst.ERRCODE_X, "已存在该昵称，请换一个");
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userInfo.getUserId()));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        Update update = new Update();
        update.set("nickName", userInfo.getNickName());
        if(!StringUtils.isEmpty(userInfo.getRealName())){
            update.set("realName", userInfo.getRealName());
        }

        update.set("mbn", userInfo.getMbn());
        update.set("gmtModified", Calendar.getInstance().getTime());

        UserInfo userInfo1 = mongoTemplate.findAndModify(query, update, UserInfo.class);

        if(userInfo1 == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }

        return true;
    }

    public Page getUserInfos(int pageNo, int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(0));
        query.addCriteria(Criteria.where("type").ne(0));

        Long totalNum = mongoTemplate.count(query, UserInfo.class);

        Page page = new Page(totalNum.intValue(), pageNo, pageSize);
        query.with(new Sort(Sort.Direction.DESC, "gmtCreate"));
        query.skip((pageNo-1) * pageSize);
        query.limit(pageSize);

        page.setList(mongoTemplate.find(query, UserInfo.class));
        return page;
    }

    public UserInfo getSingleUserInfoById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        UserInfo userInfo = mongoTemplate.findOne(query, UserInfo.class);
        if(userInfo == null){
            throw new MessageException(StringConst.ERRCODE_X, "未找到该用户");
        }

        return userInfo;
    }

    public Map<Integer, UserInfo> getUserInfosByUserIds(Set<Integer> list) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").in(list));
        query.addCriteria(Criteria.where("isDeleted").is(0));

        List<UserInfo> userInfos = mongoTemplate.find(query, UserInfo.class);
        Map<Integer, UserInfo> map = new HashMap<>();
        for(UserInfo userInfo : userInfos){
            map.put(userInfo.getUserId(), userInfo);
        }
        return map;
    }

}

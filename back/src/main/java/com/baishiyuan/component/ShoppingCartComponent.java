package com.baishiyuan.component;

import com.baishiyuan.DTO.ShoppingCartDTO;
import com.baishiyuan.domain.Goods;
import com.baishiyuan.domain.ShoppingCart;
import com.baishiyuan.vo.ShoppingCartVO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by Administrator on 2019/3/31 0031.
 */
@Component("shoppingCartComponent")
public class ShoppingCartComponent {
    @Resource(name="mongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    protected Mapper dozerMapper;

    public ShoppingCart DTOToShoppingCart(ShoppingCartDTO shoppingCartDTO, Date now, int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        dozerMapper.map(shoppingCartDTO, shoppingCart);
        shoppingCart.setGmtCreate(now);
        shoppingCart.setUserId(userId);
        return shoppingCart;
    }

    public List<ShoppingCartVO> queryShoppingCartVOs(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        List<ShoppingCart> shoppingCarts = mongoTemplate.find(query, ShoppingCart.class);
        if(CollectionUtils.isEmpty(shoppingCarts)) {
            return new ArrayList<>();
        }

        Set<String> goodsIds = new HashSet<>();
        for(ShoppingCart shoppingCart : shoppingCarts) {
            goodsIds.add(shoppingCart.getGoodsId());
        }

        query = new Query();
        query.addCriteria(Criteria.where("id").in(goodsIds));
        List<Goods> goodss = mongoTemplate.find(query, Goods.class);
        Map<String, Goods> map = new HashMap<>();
        for(Goods goods : goodss) {
            map.put(goods.getId(), goods);
        }

        List<String> expireShoppingCartsIds = new ArrayList<>();
        List<ShoppingCartVO> shoppingCartVOS = new ArrayList<>();
        for(ShoppingCart shoppingCart : shoppingCarts) {
            if(!map.containsKey(shoppingCart.getGoodsId())) {
                expireShoppingCartsIds.add(shoppingCart.getId());
            }else {
                Goods goods = map.get(shoppingCart.getGoodsId());
                ShoppingCartVO shoppingCartVO = new ShoppingCartVO();
                dozerMapper.map(goods, shoppingCartVO);
                shoppingCartVO.setId(shoppingCart.getId());
                shoppingCartVO.setNumber(shoppingCart.getNumber());
                shoppingCartVO.setGoodsId(shoppingCart.getGoodsId());
                shoppingCartVO.setGmtCreate(shoppingCart.getGmtCreate());
                shoppingCartVO.setUserId(userId);
                shoppingCartVOS.add(shoppingCartVO);
            }
        }

        if(!CollectionUtils.isEmpty(expireShoppingCartsIds)) {
            query = new Query();
            query.addCriteria(Criteria.where("id").in(expireShoppingCartsIds));
            mongoTemplate.findAllAndRemove(query, ShoppingCart.class);
        }

        return shoppingCartVOS;
    }

    public List<ShoppingCart> queryShoppingCarts(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        List<ShoppingCart> shoppingCarts = mongoTemplate.find(query, ShoppingCart.class);
        if (CollectionUtils.isEmpty(shoppingCarts)) {
            return new ArrayList<>();
        }

        return shoppingCarts;
    }

    /**根据用户ID查询产品型号*/
    public long queryNumbers(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        long totalNumber = mongoTemplate.count(query, ShoppingCart.class);
        return totalNumber;
    }

    public boolean deleteGoodsInShoppingCartByUserId(int userId, List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        query.addCriteria(Criteria.where("id").in(ids));
        mongoTemplate.remove(query, ShoppingCart.class);
        return true;
    }

}

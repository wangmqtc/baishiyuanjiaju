package com.baishiyuan.controller;

import com.baishiyuan.DTO.ShoppingCartCreateDTO;
import com.baishiyuan.DTO.ShoppingCartDTO;
import com.baishiyuan.component.ShoppingCartComponent;
import com.baishiyuan.domain.Goods;
import com.baishiyuan.domain.SessionInfo;
import com.baishiyuan.domain.ShoppingCart;
import com.baishiyuan.domain.WebResult;
import com.baishiyuan.exception.MessageException;
import com.baishiyuan.utils.StringConst;
import com.baishiyuan.utils.UserSessionFunCallUtil;
import com.baishiyuan.vo.ShoppingCartVO;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/shoppingcart")
public class ShoppingCartController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    protected Mapper dozerMapper;

    @Resource
    private ShoppingCartComponent shoppingCartComponent;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public WebResult createShoppingCart(@RequestBody @Validated ShoppingCartCreateDTO goods, HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);

        Set<String> goodsIds = new HashSet<>();
        Map<String, ShoppingCartDTO> map = new HashMap<>();
        for(ShoppingCartDTO shoppingCartDTO : goods.getGoods()) {
            goodsIds.add(shoppingCartDTO.getGoodsId());
            if(map.get(shoppingCartDTO.getGoodsId()) != null) {
                ShoppingCartDTO shoppingCartDTOTemp = map.get(shoppingCartDTO.getGoodsId());
                shoppingCartDTOTemp.setNumber(shoppingCartDTO.getNumber() + shoppingCartDTOTemp.getNumber());
            }else {
                map.put(shoppingCartDTO.getGoodsId(), shoppingCartDTO);
            }
        }

        /**查询有无过期产品*/
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(goodsIds));
        long totalNumber = mongoTemplate.count(query, Goods.class);
        if(totalNumber != goodsIds.size()) {
            throw new MessageException(StringConst.ERRCODE_X, "当前有产品是过期的！");
        }

        /**查询是否购物车中已经有有此产品没*/
        query = new Query();
        query.addCriteria(Criteria.where("userId").is(sessionInfo.getUserId()));
        query.addCriteria(Criteria.where("goodsId").in(goodsIds));
        List<ShoppingCart> existsShoppingCarts = mongoTemplate.find(query, ShoppingCart.class);
        if(existsShoppingCarts == null) {
            existsShoppingCarts = new ArrayList<>();
        }
        Map<String, ShoppingCart> existsMap = new HashMap<>();
        for(ShoppingCart existShoppingCart : existsShoppingCarts) {
            existsMap.put(existShoppingCart.getGoodsId(), existShoppingCart);
        }

        /**添加购物车数据*/
        List<ShoppingCart> newShoppingCarts = new ArrayList<>();
        Date now = Calendar.getInstance().getTime();
        for(String goodsId : map.keySet()) {
            if(existsMap.containsKey(goodsId)) {
                ShoppingCart existShoppingCart = existsMap.get(goodsId);
                existShoppingCart.setNumber(existShoppingCart.getNumber() + map.get(goodsId).getNumber());
                mongoTemplate.save(existShoppingCart);
            }else {
                ShoppingCartDTO shoppingCartDTO = map.get(goodsId);
                ShoppingCart shoppingCart = shoppingCartComponent.DTOToShoppingCart(shoppingCartDTO, now, sessionInfo.getUserId());
                newShoppingCarts.add(shoppingCart);
            }
        }

        mongoTemplate.insertAll(newShoppingCarts);

        return new WebResult(StringConst.ERRCODE_SUCCESS, "加入购物车成功", 1);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = "application/json")
    public WebResult deleteShoppingCart(@RequestParam String cartIds, HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);

        String[] cartIdsStr = cartIds.split(",");
        List<String> cartIdList = new ArrayList<>();
        for(String cartId : cartIdsStr) {
            cartIdList.add(cartId);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(sessionInfo.getUserId()));
        query.addCriteria(Criteria.where("id").in(cartIdList));
        mongoTemplate.remove(query, ShoppingCart.class);

        List<ShoppingCartVO> shoppingCartVOS = shoppingCartComponent.queryShoppingCartVOs(sessionInfo.getUserId());

        return new WebResult(StringConst.ERRCODE_SUCCESS, "删除购物车成功", shoppingCartVOS);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public WebResult getShoppingCarts(HttpServletRequest request) {
        SessionInfo sessionInfo = getSession(request);

        List<ShoppingCartVO> shoppingCartVOS = shoppingCartComponent.queryShoppingCartVOs(sessionInfo.getUserId());
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", shoppingCartVOS);
    }

    @RequestMapping(value = "/queryNumbers")
    public WebResult queryNumbers(HttpServletRequest request) {
        SessionInfo sessionInfo = testCheckShoppingCartAuth(request);

        long number = shoppingCartComponent.queryNumbers(sessionInfo.getUserId());
        return new WebResult(StringConst.ERRCODE_SUCCESS, "查询成功", number);
    }

    private SessionInfo checkShoppingCartAuth(HttpServletRequest request) {
        SessionInfo sessionInfo = UserSessionFunCallUtil.getCurrentSession(request);
        if (sessionInfo == null) {
            throw new MessageException(StringConst.ERRCODE_MUSTLOGIN, "你没有登录！");
        }

        if (sessionInfo.getType() == null) {
            throw new MessageException(StringConst.ERRCODE_X, "你的类型为空！");
        }

        return sessionInfo;
    }

    private SessionInfo testCheckShoppingCartAuth(HttpServletRequest request) {
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

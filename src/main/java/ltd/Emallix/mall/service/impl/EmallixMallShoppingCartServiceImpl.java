
package ltd.Emallix.mall.service.impl;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallShoppingCartItemVO;
import ltd.Emallix.mall.dao.EmallixMallGoodsMapper;
import ltd.Emallix.mall.dao.EmallixMallShoppingCartItemMapper;
import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.entity.EmallixMallShoppingCartItem;
import ltd.Emallix.mall.service.EmallixMallShoppingCartService;
import ltd.Emallix.mall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmallixMallShoppingCartServiceImpl implements EmallixMallShoppingCartService {

    @Autowired
    private EmallixMallShoppingCartItemMapper EmallixMallShoppingCartItemMapper;

    @Autowired
    private EmallixMallGoodsMapper EmallixMallGoodsMapper;

    @Override
    public String saveEmallixMallCartItem(EmallixMallShoppingCartItem EmallixMallShoppingCartItem) {
        EmallixMallShoppingCartItem temp = EmallixMallShoppingCartItemMapper.selectByUserIdAndGoodsId(EmallixMallShoppingCartItem.getUserId(), EmallixMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            temp.setGoodsCount(EmallixMallShoppingCartItem.getGoodsCount());
            return updateEmallixMallCartItem(temp);
        }
        EmallixMallGoods EmallixMallGoods = EmallixMallGoodsMapper.selectByPrimaryKey(EmallixMallShoppingCartItem.getGoodsId());
        //商品为空
        if (EmallixMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = EmallixMallShoppingCartItemMapper.selectCountByUserId(EmallixMallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (EmallixMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (EmallixMallShoppingCartItemMapper.insertSelective(EmallixMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateEmallixMallCartItem(EmallixMallShoppingCartItem EmallixMallShoppingCartItem) {
        EmallixMallShoppingCartItem EmallixMallShoppingCartItemUpdate = EmallixMallShoppingCartItemMapper.selectByPrimaryKey(EmallixMallShoppingCartItem.getCartItemId());
        if (EmallixMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (EmallixMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //当前登录账号的userId与待修改的cartItem中userId不同，返回错误
        if (!EmallixMallShoppingCartItemUpdate.getUserId().equals(EmallixMallShoppingCartItem.getUserId())) {
            return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
        }
        //数值相同，则不执行数据操作
        if (EmallixMallShoppingCartItem.getGoodsCount().equals(EmallixMallShoppingCartItemUpdate.getGoodsCount())) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        EmallixMallShoppingCartItemUpdate.setGoodsCount(EmallixMallShoppingCartItem.getGoodsCount());
        EmallixMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (EmallixMallShoppingCartItemMapper.updateByPrimaryKeySelective(EmallixMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public EmallixMallShoppingCartItem getEmallixMallCartItemById(Long EmallixMallShoppingCartItemId) {
        return EmallixMallShoppingCartItemMapper.selectByPrimaryKey(EmallixMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long shoppingCartItemId, Long userId) {
        EmallixMallShoppingCartItem EmallixMallShoppingCartItem = EmallixMallShoppingCartItemMapper.selectByPrimaryKey(shoppingCartItemId);
        if (EmallixMallShoppingCartItem == null) {
            return false;
        }
        //userId不同不能删除
        if (!userId.equals(EmallixMallShoppingCartItem.getUserId())) {
            return false;
        }
        return EmallixMallShoppingCartItemMapper.deleteByPrimaryKey(shoppingCartItemId) > 0;
    }

    @Override
    public List<EmallixMallShoppingCartItemVO> getMyShoppingCartItems(Long EmallixMallUserId) {
        List<EmallixMallShoppingCartItemVO> EmallixMallShoppingCartItemVOS = new ArrayList<>();
        List<EmallixMallShoppingCartItem> EmallixMallShoppingCartItems = EmallixMallShoppingCartItemMapper.selectByUserId(EmallixMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(EmallixMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> EmallixMallGoodsIds = EmallixMallShoppingCartItems.stream().map(EmallixMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<EmallixMallGoods> EmallixMallGoods = EmallixMallGoodsMapper.selectByPrimaryKeys(EmallixMallGoodsIds);
            Map<Long, EmallixMallGoods> EmallixMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(EmallixMallGoods)) {
                EmallixMallGoodsMap = EmallixMallGoods.stream().collect(Collectors.toMap(ltd.Emallix.mall.entity.EmallixMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (EmallixMallShoppingCartItem EmallixMallShoppingCartItem : EmallixMallShoppingCartItems) {
                EmallixMallShoppingCartItemVO EmallixMallShoppingCartItemVO = new EmallixMallShoppingCartItemVO();
                BeanUtil.copyProperties(EmallixMallShoppingCartItem, EmallixMallShoppingCartItemVO);
                if (EmallixMallGoodsMap.containsKey(EmallixMallShoppingCartItem.getGoodsId())) {
                    EmallixMallGoods EmallixMallGoodsTemp = EmallixMallGoodsMap.get(EmallixMallShoppingCartItem.getGoodsId());
                    EmallixMallShoppingCartItemVO.setGoodsCoverImg(EmallixMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = EmallixMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    EmallixMallShoppingCartItemVO.setGoodsName(goodsName);
                    EmallixMallShoppingCartItemVO.setSellingPrice(EmallixMallGoodsTemp.getSellingPrice());
                    EmallixMallShoppingCartItemVOS.add(EmallixMallShoppingCartItemVO);
                }
            }
        }
        return EmallixMallShoppingCartItemVOS;
    }
}

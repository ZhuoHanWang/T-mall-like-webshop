
package ltd.Emallix.mall.service;

import ltd.Emallix.mall.controller.vo.EmallixMallShoppingCartItemVO;
import ltd.Emallix.mall.entity.EmallixMallShoppingCartItem;

import java.util.List;

public interface EmallixMallShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param EmallixMallShoppingCartItem
     * @return
     */
    String saveEmallixMallCartItem(EmallixMallShoppingCartItem EmallixMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param EmallixMallShoppingCartItem
     * @return
     */
    String updateEmallixMallCartItem(EmallixMallShoppingCartItem EmallixMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param EmallixMallShoppingCartItemId
     * @return
     */
    EmallixMallShoppingCartItem getEmallixMallCartItemById(Long EmallixMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     *
     * @param shoppingCartItemId
     * @param userId
     * @return
     */
    Boolean deleteById(Long shoppingCartItemId, Long userId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param EmallixMallUserId
     * @return
     */
    List<EmallixMallShoppingCartItemVO> getMyShoppingCartItems(Long EmallixMallUserId);
}

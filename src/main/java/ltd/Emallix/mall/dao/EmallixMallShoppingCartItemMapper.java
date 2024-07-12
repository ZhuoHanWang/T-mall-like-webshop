
package ltd.Emallix.mall.dao;

import ltd.Emallix.mall.entity.EmallixMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmallixMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(EmallixMallShoppingCartItem record);

    int insertSelective(EmallixMallShoppingCartItem record);

    EmallixMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    EmallixMallShoppingCartItem selectByUserIdAndGoodsId(@Param("EmallixMallUserId") Long EmallixMallUserId, @Param("goodsId") Long goodsId);

    List<EmallixMallShoppingCartItem> selectByUserId(@Param("EmallixMallUserId") Long EmallixMallUserId, @Param("number") int number);

    int selectCountByUserId(Long EmallixMallUserId);

    int updateByPrimaryKeySelective(EmallixMallShoppingCartItem record);

    int updateByPrimaryKey(EmallixMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}
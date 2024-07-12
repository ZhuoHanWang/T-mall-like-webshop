
package ltd.Emallix.mall.dao;

import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.entity.StockNumDTO;
import ltd.Emallix.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmallixMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(EmallixMallGoods record);

    int insertSelective(EmallixMallGoods record);

    EmallixMallGoods selectByPrimaryKey(Long goodsId);

    EmallixMallGoods selectByCategoryIdAndName(@Param("goodsName") String goodsName, @Param("goodsCategoryId") Long goodsCategoryId);

    int updateByPrimaryKeySelective(EmallixMallGoods record);

    int updateByPrimaryKeyWithBLOBs(EmallixMallGoods record);

    int updateByPrimaryKey(EmallixMallGoods record);

    List<EmallixMallGoods> findEmallixMallGoodsList(PageQueryUtil pageUtil);

    int getTotalEmallixMallGoods(PageQueryUtil pageUtil);

    List<EmallixMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<EmallixMallGoods> findEmallixMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalEmallixMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("EmallixMallGoodsList") List<EmallixMallGoods> EmallixMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int recoverStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds")Long[] orderIds,@Param("sellStatus") int sellStatus);

}
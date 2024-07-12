
package ltd.Emallix.mall.service;

import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;

import java.util.List;

public interface EmallixMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getEmallixMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveEmallixMallGoods(EmallixMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param EmallixMallGoodsList
     * @return
     */
    void batchSaveEmallixMallGoods(List<EmallixMallGoods> EmallixMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateEmallixMallGoods(EmallixMallGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    EmallixMallGoods getEmallixMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids,int sellStatus);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchEmallixMallGoods(PageQueryUtil pageUtil);
}

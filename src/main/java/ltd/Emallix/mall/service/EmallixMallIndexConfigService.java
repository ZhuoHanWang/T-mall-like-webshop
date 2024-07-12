
package ltd.Emallix.mall.service;

import ltd.Emallix.mall.controller.vo.EmallixMallIndexConfigGoodsVO;
import ltd.Emallix.mall.entity.IndexConfig;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;

import java.util.List;

public interface EmallixMallIndexConfigService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getConfigsPage(PageQueryUtil pageUtil);

    String saveIndexConfig(IndexConfig indexConfig);

    String updateIndexConfig(IndexConfig indexConfig);

    IndexConfig getIndexConfigById(Long id);

    /**
     * 返回固定数量的首页配置商品对象(首页调用)
     *
     * @param number
     * @return
     */
    List<EmallixMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number);

    Boolean deleteBatch(Long[] ids);
}

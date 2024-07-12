
package ltd.Emallix.mall.service;

import ltd.Emallix.mall.controller.vo.EmallixMallIndexCarouselVO;
import ltd.Emallix.mall.entity.Carousel;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;

import java.util.List;

public interface EmallixMallCarouselService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<EmallixMallIndexCarouselVO> getCarouselsForIndex(int number);
}

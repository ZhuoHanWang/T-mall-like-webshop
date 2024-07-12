
package ltd.Emallix.mall.controller.mall;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.IndexConfigTypeEnum;
import ltd.Emallix.mall.common.EmallixException;
import ltd.Emallix.mall.controller.vo.EmallixMallIndexCarouselVO;
import ltd.Emallix.mall.controller.vo.EmallixMallIndexCategoryVO;
import ltd.Emallix.mall.controller.vo.EmallixMallIndexConfigGoodsVO;
import ltd.Emallix.mall.service.EmallixMallCarouselService;
import ltd.Emallix.mall.service.EmallixMallCategoryService;
import ltd.Emallix.mall.service.EmallixMallIndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


// 这里是整个服务的首页
@Controller
public class IndexController {

    @Resource
    private EmallixMallCarouselService EmallixMallCarouselService;

    @Resource
    private EmallixMallIndexConfigService EmallixMallIndexConfigService;

    @Resource
    private EmallixMallCategoryService EmallixMallCategoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<EmallixMallIndexCategoryVO> categories = EmallixMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            EmallixException.fail("分类数据不完善");
        }
        List<EmallixMallIndexCarouselVO> carousels = EmallixMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<EmallixMallIndexConfigGoodsVO> hotGoodses = EmallixMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<EmallixMallIndexConfigGoodsVO> newGoodses = EmallixMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<EmallixMallIndexConfigGoodsVO> recommendGoodses = EmallixMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}

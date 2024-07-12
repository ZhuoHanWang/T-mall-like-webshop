
package ltd.Emallix.mall.controller.mall;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.EmallixException;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallGoodsDetailVO;
import ltd.Emallix.mall.controller.vo.SearchPageCategoryVO;
import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.service.EmallixMallCategoryService;
import ltd.Emallix.mall.service.EmallixMallGoodsService;
import ltd.Emallix.mall.util.BeanUtil;
import ltd.Emallix.mall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private EmallixMallGoodsService EmallixMallGoodsService;
    @Resource
    private EmallixMallCategoryService EmallixMallCategoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据
        if (params.containsKey("goodsCategoryId") && StringUtils.hasText(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = EmallixMallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && StringUtils.hasText(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && StringUtils.hasText((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //搜索上架状态下的商品
        params.put("goodsSellStatus", Constants.SELL_STATUS_UP);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", EmallixMallGoodsService.searchEmallixMallGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            EmallixException.fail("参数异常");
        }
        EmallixMallGoods goods = EmallixMallGoodsService.getEmallixMallGoodsById(goodsId);
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()) {
            EmallixException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        EmallixMallGoodsDetailVO goodsDetailVO = new EmallixMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

}

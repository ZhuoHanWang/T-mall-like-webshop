
package ltd.Emallix.mall.controller.admin;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.EmallixCategoryLevelEnum;
import ltd.Emallix.mall.common.EmallixException;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.entity.GoodsCategory;
import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.service.EmallixMallCategoryService;
import ltd.Emallix.mall.service.EmallixMallGoodsService;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.Result;
import ltd.Emallix.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Controller
@RequestMapping("/admin")
public class EmallixGoodsController {

    @Resource
    private EmallixMallGoodsService EmallixMallGoodsService;
    @Resource
    private EmallixMallCategoryService EmallixMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "Emallix_mall_goods");
        return "admin/Emallix_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), EmallixCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), EmallixCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), EmallixCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/Emallix_mall_goods_edit";
            }
        }
        EmallixException.fail("分类数据不完善");
        return null;
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        EmallixMallGoods EmallixMallGoods = EmallixMallGoodsService.getEmallixMallGoodsById(goodsId);
        if (EmallixMallGoods.getGoodsCategoryId() > 0) {
            if (EmallixMallGoods.getGoodsCategoryId() != null || EmallixMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = EmallixMallCategoryService.getGoodsCategoryById(EmallixMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == EmallixCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), EmallixCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), EmallixCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = EmallixMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), EmallixCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = EmallixMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (EmallixMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), EmallixCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), EmallixCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = EmallixMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), EmallixCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", EmallixMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/Emallix_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(EmallixMallGoodsService.getEmallixMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody EmallixMallGoods EmallixMallGoods) {
        if (!StringUtils.hasText(EmallixMallGoods.getGoodsName())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsIntro())
                || !StringUtils.hasText(EmallixMallGoods.getTag())
                || Objects.isNull(EmallixMallGoods.getOriginalPrice())
                || Objects.isNull(EmallixMallGoods.getGoodsCategoryId())
                || Objects.isNull(EmallixMallGoods.getSellingPrice())
                || Objects.isNull(EmallixMallGoods.getStockNum())
                || Objects.isNull(EmallixMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = EmallixMallGoodsService.saveEmallixMallGoods(EmallixMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody EmallixMallGoods EmallixMallGoods) {
        if (Objects.isNull(EmallixMallGoods.getGoodsId())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsName())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsIntro())
                || !StringUtils.hasText(EmallixMallGoods.getTag())
                || Objects.isNull(EmallixMallGoods.getOriginalPrice())
                || Objects.isNull(EmallixMallGoods.getSellingPrice())
                || Objects.isNull(EmallixMallGoods.getGoodsCategoryId())
                || Objects.isNull(EmallixMallGoods.getStockNum())
                || Objects.isNull(EmallixMallGoods.getGoodsSellStatus())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsCoverImg())
                || !StringUtils.hasText(EmallixMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = EmallixMallGoodsService.updateEmallixMallGoods(EmallixMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        EmallixMallGoods goods = EmallixMallGoodsService.getEmallixMallGoodsById(id);
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (EmallixMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}
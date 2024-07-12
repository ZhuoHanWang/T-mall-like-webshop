
package ltd.Emallix.mall.service.impl;

import ltd.Emallix.mall.common.EmallixCategoryLevelEnum;
import ltd.Emallix.mall.common.EmallixException;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallSearchGoodsVO;
import ltd.Emallix.mall.dao.EmallixMallGoodsMapper;
import ltd.Emallix.mall.dao.GoodsCategoryMapper;
import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.entity.GoodsCategory;
import ltd.Emallix.mall.service.EmallixMallGoodsService;
import ltd.Emallix.mall.util.BeanUtil;
import ltd.Emallix.mall.util.EmallixMallUtils;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class EmallixMallGoodsServiceImpl implements EmallixMallGoodsService {

    @Autowired
    private EmallixMallGoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public PageResult getEmallixMallGoodsPage(PageQueryUtil pageUtil) {
        List<EmallixMallGoods> goodsList = goodsMapper.findEmallixMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalEmallixMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveEmallixMallGoods(EmallixMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != EmallixCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        if (goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId()) != null) {
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(EmallixMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(EmallixMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(EmallixMallUtils.cleanString(goods.getTag()));
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveEmallixMallGoods(List<EmallixMallGoods> EmallixMallGoodsList) {
        if (!CollectionUtils.isEmpty(EmallixMallGoodsList)) {
            goodsMapper.batchInsert(EmallixMallGoodsList);
        }
    }

    @Override
    public String updateEmallixMallGoods(EmallixMallGoods goods) {
        GoodsCategory goodsCategory = goodsCategoryMapper.selectByPrimaryKey(goods.getGoodsCategoryId());
        // 分类不存在或者不是三级分类，则该参数字段异常
        if (goodsCategory == null || goodsCategory.getCategoryLevel().intValue() != EmallixCategoryLevelEnum.LEVEL_THREE.getLevel()) {
            return ServiceResultEnum.GOODS_CATEGORY_ERROR.getResult();
        }
        EmallixMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        EmallixMallGoods temp2 = goodsMapper.selectByCategoryIdAndName(goods.getGoodsName(), goods.getGoodsCategoryId());
        if (temp2 != null && !temp2.getGoodsId().equals(goods.getGoodsId())) {
            //name和分类id相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_GOODS_EXIST.getResult();
        }
        goods.setGoodsName(EmallixMallUtils.cleanString(goods.getGoodsName()));
        goods.setGoodsIntro(EmallixMallUtils.cleanString(goods.getGoodsIntro()));
        goods.setTag(EmallixMallUtils.cleanString(goods.getTag()));
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public EmallixMallGoods getEmallixMallGoodsById(Long id) {
        EmallixMallGoods EmallixMallGoods = goodsMapper.selectByPrimaryKey(id);
        if (EmallixMallGoods == null) {
            EmallixException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        return EmallixMallGoods;
    }

    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchEmallixMallGoods(PageQueryUtil pageUtil) {
        List<EmallixMallGoods> goodsList = goodsMapper.findEmallixMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalEmallixMallGoodsBySearch(pageUtil);
        List<EmallixMallSearchGoodsVO> EmallixMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            EmallixMallSearchGoodsVOS = BeanUtil.copyList(goodsList, EmallixMallSearchGoodsVO.class);
            for (EmallixMallSearchGoodsVO EmallixMallSearchGoodsVO : EmallixMallSearchGoodsVOS) {
                String goodsName = EmallixMallSearchGoodsVO.getGoodsName();
                String goodsIntro = EmallixMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    EmallixMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    EmallixMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(EmallixMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}

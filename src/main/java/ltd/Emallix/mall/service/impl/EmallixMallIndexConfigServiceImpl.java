
package ltd.Emallix.mall.service.impl;

import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallIndexConfigGoodsVO;
import ltd.Emallix.mall.dao.EmallixMallGoodsMapper;
import ltd.Emallix.mall.dao.IndexConfigMapper;
import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.entity.IndexConfig;
import ltd.Emallix.mall.service.EmallixMallIndexConfigService;
import ltd.Emallix.mall.util.BeanUtil;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmallixMallIndexConfigServiceImpl implements EmallixMallIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private EmallixMallGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        if (goodsMapper.selectByPrimaryKey(indexConfig.getGoodsId()) == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId()) != null) {
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        if (goodsMapper.selectByPrimaryKey(indexConfig.getGoodsId()) == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        IndexConfig temp2 = indexConfigMapper.selectByTypeAndGoodsId(indexConfig.getConfigType(), indexConfig.getGoodsId());
        if (temp2 != null && !temp2.getConfigId().equals(indexConfig.getConfigId())) {
            //goodsId相同且不同id 不能继续修改
            return ServiceResultEnum.SAME_INDEX_CONFIG_EXIST.getResult();
        }
        indexConfig.setUpdateTime(new Date());
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<EmallixMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<EmallixMallIndexConfigGoodsVO> EmallixMallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<EmallixMallGoods> EmallixMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            EmallixMallIndexConfigGoodsVOS = BeanUtil.copyList(EmallixMallGoods, EmallixMallIndexConfigGoodsVO.class);
            for (EmallixMallIndexConfigGoodsVO EmallixMallIndexConfigGoodsVO : EmallixMallIndexConfigGoodsVOS) {
                String goodsName = EmallixMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = EmallixMallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    EmallixMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    EmallixMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return EmallixMallIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}

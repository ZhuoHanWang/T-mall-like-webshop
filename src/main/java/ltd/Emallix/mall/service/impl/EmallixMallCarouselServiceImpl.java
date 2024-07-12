
package ltd.Emallix.mall.service.impl;

import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallIndexCarouselVO;
import ltd.Emallix.mall.dao.CarouselMapper;
import ltd.Emallix.mall.entity.Carousel;
import ltd.Emallix.mall.service.EmallixMallCarouselService;
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

@Transactional
@Service
public class EmallixMallCarouselServiceImpl implements EmallixMallCarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public PageResult getCarouselPage(PageQueryUtil pageUtil) {
        List<Carousel> carousels = carouselMapper.findCarouselList(pageUtil);
        int total = carouselMapper.getTotalCarousels(pageUtil);
        PageResult pageResult = new PageResult(carousels, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveCarousel(Carousel carousel) {
        if (carouselMapper.insertSelective(carousel) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateCarousel(Carousel carousel) {
        Carousel temp = carouselMapper.selectByPrimaryKey(carousel.getCarouselId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        temp.setCarouselRank(carousel.getCarouselRank());
        temp.setRedirectUrl(carousel.getRedirectUrl());
        temp.setCarouselUrl(carousel.getCarouselUrl());
        temp.setUpdateTime(new Date());
        if (carouselMapper.updateByPrimaryKeySelective(temp) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public Carousel getCarouselById(Integer id) {
        return carouselMapper.selectByPrimaryKey(id);
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return carouselMapper.deleteBatch(ids) > 0;
    }

    @Override
    public List<EmallixMallIndexCarouselVO> getCarouselsForIndex(int number) {
        List<EmallixMallIndexCarouselVO> EmallixMallIndexCarouselVOS = new ArrayList<>(number);
        List<Carousel> carousels = carouselMapper.findCarouselsByNum(number);
        if (!CollectionUtils.isEmpty(carousels)) {
            EmallixMallIndexCarouselVOS = BeanUtil.copyList(carousels, EmallixMallIndexCarouselVO.class);
        }
        return EmallixMallIndexCarouselVOS;
    }
}

package ltd.Emallix.mall.dao;
import ltd.Emallix.mall.entity.EmallixMallOrder;
import ltd.Emallix.mall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmallixMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(EmallixMallOrder record);

    int insertSelective(EmallixMallOrder record);

    EmallixMallOrder selectByPrimaryKey(Long orderId);

    EmallixMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(EmallixMallOrder record);

    int updateByPrimaryKey(EmallixMallOrder record);

    List<EmallixMallOrder> findEmallixMallOrderList(PageQueryUtil pageUtil);

    int getTotalEmallixMallOrders(PageQueryUtil pageUtil);

    List<EmallixMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}
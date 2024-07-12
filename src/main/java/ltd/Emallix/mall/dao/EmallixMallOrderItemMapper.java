
package ltd.Emallix.mall.dao;

import ltd.Emallix.mall.entity.EmallixMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface EmallixMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(EmallixMallOrderItem record);

    int insertSelective(EmallixMallOrderItem record);

    EmallixMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<EmallixMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<EmallixMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<EmallixMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(EmallixMallOrderItem record);

    int updateByPrimaryKey(EmallixMallOrderItem record);
}
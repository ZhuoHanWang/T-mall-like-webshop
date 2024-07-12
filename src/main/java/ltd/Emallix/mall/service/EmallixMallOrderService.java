
package ltd.Emallix.mall.service;

import ltd.Emallix.mall.controller.vo.EmallixMallOrderDetailVO;
import ltd.Emallix.mall.controller.vo.EmallixMallOrderItemVO;
import ltd.Emallix.mall.controller.vo.EmallixMallShoppingCartItemVO;
import ltd.Emallix.mall.controller.vo.EmallixMallUserVO;
import ltd.Emallix.mall.entity.EmallixMallOrder;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;

import java.util.List;

public interface EmallixMallOrderService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getEmallixMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param EmallixMallOrder
     * @return
     */
    String updateOrderInfo(EmallixMallOrder EmallixMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(EmallixMallUserVO user, List<EmallixMallShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    EmallixMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    EmallixMallOrder getEmallixMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<EmallixMallOrderItemVO> getOrderItems(Long id);
}

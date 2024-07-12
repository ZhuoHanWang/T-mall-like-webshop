
package ltd.Emallix.mall.service.impl;

import ltd.Emallix.mall.common.*;
import ltd.Emallix.mall.controller.vo.*;
import ltd.Emallix.mall.dao.EmallixMallGoodsMapper;
import ltd.Emallix.mall.dao.EmallixMallOrderItemMapper;
import ltd.Emallix.mall.dao.EmallixMallOrderMapper;
import ltd.Emallix.mall.dao.EmallixMallShoppingCartItemMapper;
import ltd.Emallix.mall.entity.EmallixMallGoods;
import ltd.Emallix.mall.entity.EmallixMallOrder;
import ltd.Emallix.mall.entity.EmallixMallOrderItem;
import ltd.Emallix.mall.entity.StockNumDTO;
import ltd.Emallix.mall.service.EmallixMallOrderService;
import ltd.Emallix.mall.util.BeanUtil;
import ltd.Emallix.mall.util.NumberUtil;
import ltd.Emallix.mall.util.PageQueryUtil;
import ltd.Emallix.mall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class EmallixMallOrderServiceImpl implements EmallixMallOrderService {

    @Autowired
    private EmallixMallOrderMapper EmallixMallOrderMapper;
    @Autowired
    private EmallixMallOrderItemMapper EmallixMallOrderItemMapper;
    @Autowired
    private EmallixMallShoppingCartItemMapper EmallixMallShoppingCartItemMapper;
    @Autowired
    private EmallixMallGoodsMapper EmallixMallGoodsMapper;

    @Override
    public PageResult getEmallixMallOrdersPage(PageQueryUtil pageUtil) {
        List<EmallixMallOrder> EmallixMallOrders = EmallixMallOrderMapper.findEmallixMallOrderList(pageUtil);
        int total = EmallixMallOrderMapper.getTotalEmallixMallOrders(pageUtil);
        PageResult pageResult = new PageResult(EmallixMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(EmallixMallOrder EmallixMallOrder) {
        EmallixMallOrder temp = EmallixMallOrderMapper.selectByPrimaryKey(EmallixMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(EmallixMallOrder.getTotalPrice());
            temp.setUserAddress(EmallixMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (EmallixMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<EmallixMallOrder> orders = EmallixMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (EmallixMallOrder EmallixMallOrder : orders) {
                if (EmallixMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += EmallixMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (EmallixMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += EmallixMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (EmallixMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<EmallixMallOrder> orders = EmallixMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (EmallixMallOrder EmallixMallOrder : orders) {
                if (EmallixMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += EmallixMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (EmallixMallOrder.getOrderStatus() != 1 && EmallixMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += EmallixMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (EmallixMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<EmallixMallOrder> orders = EmallixMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (EmallixMallOrder EmallixMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (EmallixMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += EmallixMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (EmallixMallOrder.getOrderStatus() == 4 || EmallixMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += EmallixMallOrder.getOrderNo() + " ";
                }
            }
            if (!StringUtils.hasText(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间&&恢复库存
                if (EmallixMallOrderMapper.closeOrder(Arrays.asList(ids), EmallixOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0 && recoverStockNum(Arrays.asList(ids))) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(EmallixMallUserVO user, List<EmallixMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(EmallixMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(EmallixMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<EmallixMallGoods> EmallixMallGoods = EmallixMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<EmallixMallGoods> goodsListNotSelling = EmallixMallGoods.stream()
                .filter(EmallixMallGoodsTemp -> EmallixMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            EmallixException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, EmallixMallGoods> EmallixMallGoodsMap = EmallixMallGoods.stream().collect(Collectors.toMap(ltd.Emallix.mall.entity.EmallixMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (EmallixMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!EmallixMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                EmallixException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > EmallixMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                EmallixException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(EmallixMallGoods)) {
            if (EmallixMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = EmallixMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    EmallixException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                EmallixMallOrder EmallixMallOrder = new EmallixMallOrder();
                EmallixMallOrder.setOrderNo(orderNo);
                EmallixMallOrder.setUserId(user.getUserId());
                EmallixMallOrder.setUserAddress(user.getAddress());
                //总价
                for (EmallixMallShoppingCartItemVO EmallixMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += EmallixMallShoppingCartItemVO.getGoodsCount() * EmallixMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    EmallixException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                EmallixMallOrder.setTotalPrice(priceTotal);
                //订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                EmallixMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (EmallixMallOrderMapper.insertSelective(EmallixMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<EmallixMallOrderItem> EmallixMallOrderItems = new ArrayList<>();
                    for (EmallixMallShoppingCartItemVO EmallixMallShoppingCartItemVO : myShoppingCartItems) {
                        EmallixMallOrderItem EmallixMallOrderItem = new EmallixMallOrderItem();
                        //使用BeanUtil工具类将EmallixMallShoppingCartItemVO中的属性复制到EmallixMallOrderItem对象中
                        BeanUtil.copyProperties(EmallixMallShoppingCartItemVO, EmallixMallOrderItem);
                        //EmallixMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        EmallixMallOrderItem.setOrderId(EmallixMallOrder.getOrderId());
                        EmallixMallOrderItems.add(EmallixMallOrderItem);
                    }
                    //保存至数据库
                    if (EmallixMallOrderItemMapper.insertBatch(EmallixMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    EmallixException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                EmallixException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            EmallixException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        EmallixException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public EmallixMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderMapper.selectByOrderNo(orderNo);
        if (EmallixMallOrder == null) {
            EmallixException.fail(ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult());
        }
        //验证是否是当前userId下的订单，否则报错
        if (!userId.equals(EmallixMallOrder.getUserId())) {
            EmallixException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        List<EmallixMallOrderItem> orderItems = EmallixMallOrderItemMapper.selectByOrderId(EmallixMallOrder.getOrderId());
        //获取订单项数据
        if (CollectionUtils.isEmpty(orderItems)) {
            EmallixException.fail(ServiceResultEnum.ORDER_ITEM_NOT_EXIST_ERROR.getResult());
        }
        List<EmallixMallOrderItemVO> EmallixMallOrderItemVOS = BeanUtil.copyList(orderItems, EmallixMallOrderItemVO.class);
        EmallixMallOrderDetailVO EmallixMallOrderDetailVO = new EmallixMallOrderDetailVO();
        BeanUtil.copyProperties(EmallixMallOrder, EmallixMallOrderDetailVO);
        EmallixMallOrderDetailVO.setOrderStatusString(EmallixOrderStatusEnum.getEmallixMallOrderStatusEnumByStatus(EmallixMallOrderDetailVO.getOrderStatus()).getName());
        EmallixMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(EmallixMallOrderDetailVO.getPayType()).getName());
        EmallixMallOrderDetailVO.setEmallixMallOrderItemVOS(EmallixMallOrderItemVOS);
        return EmallixMallOrderDetailVO;
    }

    @Override
    public EmallixMallOrder getEmallixMallOrderByOrderNo(String orderNo) {
        return EmallixMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = EmallixMallOrderMapper.getTotalEmallixMallOrders(pageUtil);
        List<EmallixMallOrder> EmallixMallOrders = EmallixMallOrderMapper.findEmallixMallOrderList(pageUtil);
        List<EmallixMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(EmallixMallOrders, EmallixMallOrderListVO.class);
            //设置订单状态中文显示值
            for (EmallixMallOrderListVO EmallixMallOrderListVO : orderListVOS) {
                EmallixMallOrderListVO.setOrderStatusString(EmallixOrderStatusEnum.getEmallixMallOrderStatusEnumByStatus(EmallixMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = EmallixMallOrders.stream().map(EmallixMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<EmallixMallOrderItem> orderItems = EmallixMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<EmallixMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(EmallixMallOrderItem::getOrderId));
                for (EmallixMallOrderListVO EmallixMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(EmallixMallOrderListVO.getOrderId())) {
                        List<EmallixMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(EmallixMallOrderListVO.getOrderId());
                        //将EmallixMallOrderItem对象列表转换成EmallixMallOrderItemVO对象列表
                        List<EmallixMallOrderItemVO> EmallixMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, EmallixMallOrderItemVO.class);
                        EmallixMallOrderListVO.setEmallixMallOrderItemVOS(EmallixMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String cancelOrder(String orderNo, Long userId) {
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderMapper.selectByOrderNo(orderNo);
        if (EmallixMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(EmallixMallOrder.getUserId())) {
                EmallixException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
            }
            //订单状态判断
            if (EmallixMallOrder.getOrderStatus().intValue() == EmallixOrderStatusEnum.ORDER_SUCCESS.getOrderStatus()
                    || EmallixMallOrder.getOrderStatus().intValue() == EmallixOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()
                    || EmallixMallOrder.getOrderStatus().intValue() == EmallixOrderStatusEnum.ORDER_CLOSED_BY_EXPIRED.getOrderStatus()
                    || EmallixMallOrder.getOrderStatus().intValue() == EmallixOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            //修改订单状态&&恢复库存
            if (EmallixMallOrderMapper.closeOrder(Collections.singletonList(EmallixMallOrder.getOrderId()), EmallixOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0 && recoverStockNum(Collections.singletonList(EmallixMallOrder.getOrderId()))) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderMapper.selectByOrderNo(orderNo);
        if (EmallixMallOrder != null) {
            //验证是否是当前userId下的订单，否则报错
            if (!userId.equals(EmallixMallOrder.getUserId())) {
                return ServiceResultEnum.NO_PERMISSION_ERROR.getResult();
            }
            //订单状态判断 非出库状态下不进行修改操作
            if (EmallixMallOrder.getOrderStatus().intValue() != EmallixOrderStatusEnum.ORDER_EXPRESS.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            EmallixMallOrder.setOrderStatus((byte) EmallixOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            EmallixMallOrder.setUpdateTime(new Date());
            if (EmallixMallOrderMapper.updateByPrimaryKeySelective(EmallixMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderMapper.selectByOrderNo(orderNo);
        if (EmallixMallOrder != null) {
            //订单状态判断 非待支付状态下不进行修改操作
            if (EmallixMallOrder.getOrderStatus().intValue() != EmallixOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
                return ServiceResultEnum.ORDER_STATUS_ERROR.getResult();
            }
            EmallixMallOrder.setOrderStatus((byte) EmallixOrderStatusEnum.ORDER_PAID.getOrderStatus());
            EmallixMallOrder.setPayType((byte) payType);
            EmallixMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            EmallixMallOrder.setPayTime(new Date());
            EmallixMallOrder.setUpdateTime(new Date());
            if (EmallixMallOrderMapper.updateByPrimaryKeySelective(EmallixMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<EmallixMallOrderItemVO> getOrderItems(Long id) {
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderMapper.selectByPrimaryKey(id);
        if (EmallixMallOrder != null) {
            List<EmallixMallOrderItem> orderItems = EmallixMallOrderItemMapper.selectByOrderId(EmallixMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<EmallixMallOrderItemVO> EmallixMallOrderItemVOS = BeanUtil.copyList(orderItems, EmallixMallOrderItemVO.class);
                return EmallixMallOrderItemVOS;
            }
        }
        return null;
    }

    /**
     * 恢复库存
     * @param orderIds
     * @return
     */
    public Boolean recoverStockNum(List<Long> orderIds) {
        //查询对应的订单项
        List<EmallixMallOrderItem> EmallixMallOrderItems = EmallixMallOrderItemMapper.selectByOrderIds(orderIds);
        //获取对应的商品id和商品数量并赋值到StockNumDTO对象中
        List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(EmallixMallOrderItems, StockNumDTO.class);
        //执行恢复库存的操作
        int updateStockNumResult = EmallixMallGoodsMapper.recoverStockNum(stockNumDTOS);
        if (updateStockNumResult < 1) {
            EmallixException.fail(ServiceResultEnum.CLOSE_ORDER_ERROR.getResult());
            return false;
        } else {
            return true;
        }
    }
}

package ltd.Emallix.mall.controller.mall;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.EmallixException;
import ltd.Emallix.mall.common.EmallixOrderStatusEnum;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallOrderDetailVO;
import ltd.Emallix.mall.controller.vo.EmallixMallShoppingCartItemVO;
import ltd.Emallix.mall.controller.vo.EmallixMallUserVO;
import ltd.Emallix.mall.entity.EmallixMallOrder;
import ltd.Emallix.mall.service.EmallixMallOrderService;
import ltd.Emallix.mall.service.EmallixMallShoppingCartService;
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
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {

    @Resource
    private EmallixMallShoppingCartService EmallixMallShoppingCartService;
    @Resource
    private EmallixMallOrderService EmallixMallOrderService;

    @GetMapping("/orders/{orderNo}")
    public String orderDetailPage(HttpServletRequest request, @PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        EmallixMallOrderDetailVO orderDetailVO = EmallixMallOrderService.getOrderDetailByOrderNo(orderNo, user.getUserId());
        request.setAttribute("orderDetailVO", orderDetailVO);
        return "mall/order-detail";
    }

    @GetMapping("/orders")
    public String orderListPage(@RequestParam Map<String, Object> params, HttpServletRequest request, HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        params.put("userId", user.getUserId());
        if (ObjectUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.ORDER_SEARCH_PAGE_LIMIT);
        //封装我的订单数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("orderPageResult", EmallixMallOrderService.getMyOrders(pageUtil));
        request.setAttribute("path", "orders");
        return "mall/my-orders";
    }

    @GetMapping("/saveOrder")
    public String saveOrder(HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<EmallixMallShoppingCartItemVO> myShoppingCartItems = EmallixMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!StringUtils.hasText(user.getAddress().trim())) {
            //无收货地址
            EmallixException.fail(ServiceResultEnum.NULL_ADDRESS_ERROR.getResult());
        }
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物车中无数据则跳转至错误页
            EmallixException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        }
        //保存订单并返回订单号
        String saveOrderResult = EmallixMallOrderService.saveOrder(user, myShoppingCartItems);
        //跳转到订单详情页
        return "redirect:/orders/" + saveOrderResult;
    }

    @PutMapping("/orders/{orderNo}/cancel")
    @ResponseBody
    public Result cancelOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String cancelOrderResult = EmallixMallOrderService.cancelOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(cancelOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(cancelOrderResult);
        }
    }

    @PutMapping("/orders/{orderNo}/finish")
    @ResponseBody
    public Result finishOrder(@PathVariable("orderNo") String orderNo, HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        String finishOrderResult = EmallixMallOrderService.finishOrder(orderNo, user.getUserId());
        if (ServiceResultEnum.SUCCESS.getResult().equals(finishOrderResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(finishOrderResult);
        }
    }

    @GetMapping("/selectPayType")
    public String selectPayType(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderService.getEmallixMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!user.getUserId().equals(EmallixMallOrder.getUserId())) {
            EmallixException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if (EmallixMallOrder.getOrderStatus().intValue() != EmallixOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            EmallixException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", EmallixMallOrder.getTotalPrice());
        return "mall/pay-select";
    }

    @GetMapping("/payPage")
    public String payOrder(HttpServletRequest request, @RequestParam("orderNo") String orderNo, HttpSession httpSession, @RequestParam("payType") int payType) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        EmallixMallOrder EmallixMallOrder = EmallixMallOrderService.getEmallixMallOrderByOrderNo(orderNo);
        //判断订单userId
        if (!user.getUserId().equals(EmallixMallOrder.getUserId())) {
            EmallixException.fail(ServiceResultEnum.NO_PERMISSION_ERROR.getResult());
        }
        //判断订单状态
        if (EmallixMallOrder.getOrderStatus().intValue() != EmallixOrderStatusEnum.ORDER_PRE_PAY.getOrderStatus()) {
            EmallixException.fail(ServiceResultEnum.ORDER_STATUS_ERROR.getResult());
        }
        request.setAttribute("orderNo", orderNo);
        request.setAttribute("totalPrice", EmallixMallOrder.getTotalPrice());
        if (payType == 1) {
            return "mall/alipay";
        } else {
            return "mall/wxpay";
        }
    }

    @GetMapping("/paySuccess")
    @ResponseBody
    public Result paySuccess(@RequestParam("orderNo") String orderNo, @RequestParam("payType") int payType) {
        String payResult = EmallixMallOrderService.paySuccess(orderNo, payType);
        if (ServiceResultEnum.SUCCESS.getResult().equals(payResult)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(payResult);
        }
    }

}

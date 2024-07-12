
package ltd.Emallix.mall.controller.mall;

import ltd.Emallix.mall.common.Constants;
import ltd.Emallix.mall.common.EmallixException;
import ltd.Emallix.mall.common.ServiceResultEnum;
import ltd.Emallix.mall.controller.vo.EmallixMallShoppingCartItemVO;
import ltd.Emallix.mall.controller.vo.EmallixMallUserVO;
import ltd.Emallix.mall.entity.EmallixMallShoppingCartItem;
import ltd.Emallix.mall.service.EmallixMallShoppingCartService;
import ltd.Emallix.mall.util.Result;
import ltd.Emallix.mall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private EmallixMallShoppingCartService EmallixMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<EmallixMallShoppingCartItemVO> myShoppingCartItems = EmallixMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(EmallixMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                EmallixException.fail("购物项不能为空");
            }
            //总价
            for (EmallixMallShoppingCartItemVO EmallixMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += EmallixMallShoppingCartItemVO.getGoodsCount() * EmallixMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                EmallixException.fail("购物项价格异常");
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveEmallixMallShoppingCartItem(@RequestBody EmallixMallShoppingCartItem EmallixMallShoppingCartItem,
                                                 HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        EmallixMallShoppingCartItem.setUserId(user.getUserId());
        String saveResult = EmallixMallShoppingCartService.saveEmallixMallCartItem(EmallixMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateEmallixMallShoppingCartItem(@RequestBody EmallixMallShoppingCartItem EmallixMallShoppingCartItem,
                                                   HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        EmallixMallShoppingCartItem.setUserId(user.getUserId());
        String updateResult = EmallixMallShoppingCartService.updateEmallixMallCartItem(EmallixMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{EmallixMallShoppingCartItemId}")
    @ResponseBody
    public Result updateEmallixMallShoppingCartItem(@PathVariable("EmallixMallShoppingCartItemId") Long EmallixMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = EmallixMallShoppingCartService.deleteById(EmallixMallShoppingCartItemId,user.getUserId());
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        EmallixMallUserVO user = (EmallixMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<EmallixMallShoppingCartItemVO> myShoppingCartItems = EmallixMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (EmallixMallShoppingCartItemVO EmallixMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += EmallixMallShoppingCartItemVO.getGoodsCount() * EmallixMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                EmallixException.fail("购物项价格异常");
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}

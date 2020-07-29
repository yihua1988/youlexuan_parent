package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Cart;
import com.offcn.entity.Result;
import com.offcn.utils.CookieUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    /**
     * 购物车列表
     * @param request
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response){
        String cartListString = CookieUtil.getCookieValue(request, "cartList","UTF-8");
        if(cartListString==null || cartListString.equals("")){
            System.out.println(cartListString);
            cartListString="[]";
        }
        List<Cart> cartList_Cookie= null;
        try {

            cartList_Cookie = JSON.parseArray(cartListString, Cart.class);
           cartList_Cookie=cartService.checkCartListDataInfos(cartList_Cookie);
        } catch (Exception e) {
            e.printStackTrace();
            cartList_Cookie=new ArrayList<>();
        }

        return cartList_Cookie;
    }

    /**
     * 添加商品到购物车
     * @param request
     * @param response
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(HttpServletRequest request, HttpServletResponse response, Long itemId, Integer num){
        try {
            System.out.println("add");
            List<Cart> cartList =findCartList(request,response);//获取购物车列表
            System.out.println(cartList);
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            System.out.println(cartList);
            CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList),3600*24,"UTF-8");
            System.out.println("true");
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("false");
            return new Result(false, "添加失败");
        }
    }

}

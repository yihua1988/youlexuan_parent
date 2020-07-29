package com.offcn.cart.service;

import com.offcn.entity.Cart;

import java.util.List;

/**
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/4/24
 */
public interface CartService {
    /**
     * 添加商品到购物车
     * @param cartList 原购物车商品集合
     * @param itemId  sku编号
     * @param num    购买数量
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num );

    /**
     * 处理购物车数据一致性
     * @param carts
     * @return
     */
    List<Cart> checkCartListDataInfos(List<Cart> carts);
}

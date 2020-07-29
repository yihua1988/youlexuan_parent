package com.offcn.pojo;

import com.offcn.entity.Cart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

//@RunWith(SpringJUnit4ClassRunner.class)
public class CartDemo {

    @Test
    public void m1(){
        Cart cart= new Cart();
        cart.setSellerId("abc");
        System.out.println(cart.getSellerId());
    }
}

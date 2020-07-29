package com.offcn.page.service;

import java.util.List;

/**
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/4/17
 */
public interface ItemPageService {
    /**
     * 生成商品详细页
     * @param goodsId
     */
    boolean genItemHtml(Long goodsId);
    /**
     * 删除商品详细页
     * @param goodsIds
     * @return
     */
    public boolean deleteItemHtml(List<Long> goodsIds);
}

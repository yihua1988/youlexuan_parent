package com.offcn.entity;

import com.offcn.pojo.TbOrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Data 会自动生成getset方法
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/4/24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart  implements Serializable {
    /** 商家ID */
    private String sellerId;
    /** 商家名称 */
    private String sellerName;
    /** 购物车明细 */
    private List<TbOrderItem> orderItemList;
}

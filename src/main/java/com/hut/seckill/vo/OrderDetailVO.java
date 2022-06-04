package com.hut.seckill.vo;

import com.hut.seckill.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单详情返回对象
 * @author DUANQI
 * DateTime: 2022-05-27 14:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private Order order;

    private GoodsVO goodsVO;
}

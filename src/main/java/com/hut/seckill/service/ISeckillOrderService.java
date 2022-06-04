package com.hut.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hut.seckill.pojo.SeckillOrder;
import com.hut.seckill.pojo.User;

/**
 * <p>
 * 秒杀订单表 服务类
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-25
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    Long getResult(User user, Long goodsId);
}

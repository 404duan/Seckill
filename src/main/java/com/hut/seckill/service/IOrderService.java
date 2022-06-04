package com.hut.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hut.seckill.pojo.Order;
import com.hut.seckill.pojo.User;
import com.hut.seckill.vo.GoodsVO;
import com.hut.seckill.vo.OrderDetailVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-25
 */
public interface IOrderService extends IService<Order> {

    /**
     * 秒杀
     * @param user
     * @param goods
     * @return
     */
    Order seckill(User user, GoodsVO goods);

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    OrderDetailVO detail(Long orderId);

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    String createPath(User user, Long goodsId);

    /**
     * 校验秒杀地址
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    boolean checkPath(User user, Long goodsId, String path);

    /**
     * 校验验证码
     * @param user
     * @param goodsId
     * @param captcha
     * @return
     */
    boolean checkCaptcha(User user, Long goodsId, String captcha);
}

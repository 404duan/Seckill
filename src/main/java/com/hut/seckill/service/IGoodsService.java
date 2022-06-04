package com.hut.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hut.seckill.pojo.Goods;
import com.hut.seckill.vo.GoodsVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-25
 */
public interface IGoodsService extends IService<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVO> findGoodsVO();

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    GoodsVO findGoodsVOByGoodsId(Long goodsId);
}

package com.hut.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hut.seckill.pojo.Goods;
import com.hut.seckill.vo.GoodsVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-25
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVO> findGoodsVO();

    GoodsVO findGoodsVOByGoodsId(Long goodsId);
}

package com.hut.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hut.seckill.mapper.GoodsMapper;
import com.hut.seckill.pojo.Goods;
import com.hut.seckill.service.IGoodsService;
import com.hut.seckill.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-25
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 获取商品列表
     * @return
     */
    @Override
    public List<GoodsVO> findGoodsVO() {
        return goodsMapper.findGoodsVO();
    }

    /**
     * 获取商品详情
     * @param goodsId
     * @return
     */
    @Override
    public GoodsVO findGoodsVOByGoodsId(Long goodsId) {
        return goodsMapper.findGoodsVOByGoodsId(goodsId);
    }
}

package com.hut.seckill.vo;

import com.hut.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 详情返回对象
 * @author DUANQI
 * DateTime: 2022-05-27 10:07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVO {

    private User user;

    private GoodsVO goodsVO;

    private int secKillStatus;

    private int remainSeconds;
}

package com.hut.seckill.vo;

import com.hut.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀信息
 * @author DUANQI
 * DateTime: 2022-05-29 10:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillMessage {
    private User user;
    private Long goodsId;
}

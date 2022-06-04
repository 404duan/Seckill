package com.hut.seckill.utils;

import java.util.UUID;

/**
 * UUID工具类
 * @author DUANQI
 * DateTime: 2022-05-24 14:54
 */
public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

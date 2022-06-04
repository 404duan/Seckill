package com.hut.seckill.config;

import com.hut.seckill.pojo.User;

/**
 * @author DUANQI
 * DateTime: 2022-06-02 12:05
 */
public class UserContext {

    private static ThreadLocal<User> userHolder = new ThreadLocal<>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}

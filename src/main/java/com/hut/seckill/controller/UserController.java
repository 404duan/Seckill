package com.hut.seckill.controller;


import com.hut.seckill.pojo.User;
import com.hut.seckill.rabbitmq.MQSender;
import com.hut.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-23
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    /**
     * 用户信息（测试）
     * @param user
     * @return
     */
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

    /**
     * 消息队列（测试）
     */
    @RequestMapping("/mq")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }

    /**
     * 消息队列（测试）fanout模式
     */
    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01(){
        mqSender.send("Hello");
    }

    /**
     * direct模式
     */
    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02(){
        mqSender.send01("Hello, Red");
    }

    /**
     * direct模式
     */
    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03(){
        mqSender.send02("Hello, Green");
    }

    /**
     * topic模式
     */
    @RequestMapping("/mq/topic01")
    @ResponseBody
    public void mq04(){
        mqSender.send03("Hello, Red");
    }

    /**
     * topic模式
     */
    @RequestMapping("/mq/topic02")
    @ResponseBody
    public void mq05(){
        mqSender.send04("Hello, Green");
    }

    /**
     * headers模式
     */
    @RequestMapping("/mq/header01")
    @ResponseBody
    public void mq06(){
        mqSender.send05("Hello, Header01");
    }

    /**
     * headers模式
     */
    @RequestMapping("/mq/header02")
    @ResponseBody
    public void mq07(){
        mqSender.send06("Hello, Header02");
    }
}

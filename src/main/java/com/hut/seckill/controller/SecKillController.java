package com.hut.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hut.seckill.config.AccessLimit;
import com.hut.seckill.exception.GlobalException;
import com.hut.seckill.pojo.Order;
import com.hut.seckill.pojo.SeckillOrder;
import com.hut.seckill.pojo.User;
import com.hut.seckill.rabbitmq.MQSender;
import com.hut.seckill.service.IGoodsService;
import com.hut.seckill.service.IOrderService;
import com.hut.seckill.service.ISeckillOrderService;
import com.hut.seckill.utils.JsonUtil;
import com.hut.seckill.vo.GoodsVO;
import com.hut.seckill.vo.RespBean;
import com.hut.seckill.vo.RespBeanEnum;
import com.hut.seckill.vo.SecKillMessage;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author DUANQI
 * DateTime: 2022-05-25 10:20
 */
@Slf4j
@Controller
@RequestMapping("/secKill")
public class SecKillController implements InitializingBean {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    @Autowired
    private RedisScript<Long> script;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * @return
     */
    @RequestMapping("/doSecKill2")
    public String doSecKill2(Model model, User user, Long goodsId){
        if (user == null){
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1){
            model.addAttribute("errMsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (seckillOrder != null){
            model.addAttribute("errMsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

    /**
     * 秒杀
     * @return
     */
    @PostMapping("/{path}/doSecKill")
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        // 判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        // 库存标记，减少Redis的访问
        if (emptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 预减库存
        // Long stock = valueOperations.decrement("secKillGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("secKillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock < 0){
            emptyStockMap.put(goodsId, true);
            //valueOperations.increment("secKillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SecKillMessage secKillMessage = new SecKillMessage(user, goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(secKillMessage));
        return RespBean.success(0);

        /*GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        // 判断库存
        if (goods.getStockCount() < 1){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 判断是否重复抢购
        // SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq("goods_id", goodsId));
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        System.out.println(seckillOrder);
        if (seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        Order order = orderService.seckill(user, goods);
        return RespBean.success(order);*/
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId: 成功, -1: 秒杀失败, 0: 排队中
     */
    @GetMapping("/result")
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    /**
     * 获取秒杀地址
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5, maxCount = 5)
    @GetMapping("/path")
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    @GetMapping("/captcha")
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if (user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 生成验证码，将结果放入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(120, 30, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 180, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }

    /**
     * 系统初始化，把商品库存数量加载到Redis
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> list = goodsService.findGoodsVO();
        if (CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVO -> {
            redisTemplate.opsForValue().set("secKillGoods:" + goodsVO.getId(), goodsVO.getStockCount());
            emptyStockMap.put(goodsVO.getId(), false);
        });
    }
}

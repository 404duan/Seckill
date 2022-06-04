package com.hut.seckill.controller;

import com.hut.seckill.pojo.User;
import com.hut.seckill.service.IGoodsService;
import com.hut.seckill.vo.DetailVO;
import com.hut.seckill.vo.GoodsVO;
import com.hut.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 商品
 * @author DUANQI
 * DateTime: 2022-05-24 15:19
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 跳转商品列表页
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
//    public String toList(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String ticket){
    public String toList(Model model, User user,
                         HttpServletRequest request, HttpServletResponse response){
        /*Redis中获取页面，如果不为空，直接放回页面*/
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (StringUtils.hasLength(html)){
            return html;
        }
        /*if (!StringUtils.hasLength(ticket)){
            return "login";
        }
        // User user = (User) session.getAttribute(ticket);
        User user = userService.getUserByCookie(ticket, request, response);
        if (null == user){
            return "login";
        }*/
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVO());
        /*Redis中没有，手动渲染，存入Redis并返回*/
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (StringUtils.hasLength(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页
     */
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail2(Model model, User user, @PathVariable Long goodsId,
                           HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        /*从Redis中获取页面，如果不为空，直接返回页面*/
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (StringUtils.hasLength(html)){
            return html;
        }
        model.addAttribute("user", user);
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        Date startDate = goodsVO.getStartDate();
        Date endDate = goodsVO.getEndDate();
        Date nowDate = new Date();
        int secKillStatus; /*秒杀状态*/
        int remainSeconds; /*秒杀倒计时*/
        if (nowDate.before(startDate)){
            secKillStatus = 0; /*秒杀还没开始*/
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2; /*秒杀已结束*/
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("goods", goodsVO);
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
        if (StringUtils.hasLength(html)){
            valueOperations.set("goodsDetail:" + goodsId, html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页
     */
    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId){
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        Date startDate = goodsVO.getStartDate();
        Date endDate = goodsVO.getEndDate();
        Date nowDate = new Date();
        int secKillStatus; /*秒杀状态*/
        int remainSeconds; /*秒杀倒计时*/
        if (nowDate.before(startDate)){
            secKillStatus = 0; /*秒杀还没开始*/
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        }else if (nowDate.after(endDate)){
            secKillStatus = 2; /*秒杀已结束*/
            remainSeconds = -1;
        }else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVO detailVO = new DetailVO(user, goodsVO, secKillStatus, remainSeconds);
        return RespBean.success(detailVO);
    }
}

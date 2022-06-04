package com.hut.seckill.controller;


import com.hut.seckill.pojo.User;
import com.hut.seckill.service.IOrderService;
import com.hut.seckill.vo.OrderDetailVO;
import com.hut.seckill.vo.RespBean;
import com.hut.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author DUANQI
 * @since 2022-05-25
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    /**
     * 订单详情
     * @param user
     * @param orderId
     * @return
     */
    @GetMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if (user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVO detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}

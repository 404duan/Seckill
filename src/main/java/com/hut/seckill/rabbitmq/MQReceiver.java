package com.hut.seckill.rabbitmq;

import com.hut.seckill.pojo.SeckillOrder;
import com.hut.seckill.pojo.User;
import com.hut.seckill.service.IGoodsService;
import com.hut.seckill.service.IOrderService;
import com.hut.seckill.utils.JsonUtil;
import com.hut.seckill.vo.GoodsVO;
import com.hut.seckill.vo.SecKillMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息接收者
 * @author DUANQI
 * DateTime: 2022-05-28 14:57
 */
@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "queue")
    public void receive(Object msg){
        log.info("接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout01")
    public void receive01(Object msg){
        log.info("QUEUE01接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_fanout02")
    public void receive02(Object msg){
        log.info("QUEUE02接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct01")
    public void receive03(Object msg){
        log.info("QUEUE01接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_direct02")
    public void receive04(Object msg){
        log.info("QUEUE02接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic01")
    public void receive05(Object msg){
        log.info("QUEUE01接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_topic02")
    public void receive06(Object msg){
        log.info("QUEUE02接收消息：" + msg);
    }

    @RabbitListener(queues = "queue_header01")
    public void receive07(Message message){
        log.info("QUEUE01接收Message对象：" + message);
        log.info("QUEUE01接收消息：" + new String(message.getBody()));
    }

    @RabbitListener(queues = "queue_header02")
    public void receive08(Message message){
        log.info("QUEUE02接收Message对象：" + message);
        log.info("QUEUE02接收消息：" + new String(message.getBody()));
    }

    @RabbitListener(queues = "secKillQueue")
    public void receive(String message){
        log.info("接收的消息：" + message);
        SecKillMessage secKillMessage = JsonUtil.jsonStr2Object(message, SecKillMessage.class);
        Long goodsId = secKillMessage.getGoodsId();
        User user = secKillMessage.getUser();
        // 判断库存
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        if (goodsVO.getStockCount() < 1){
            return;
        }
        // 判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null){
            return;
        }
        /*下单操作*/
        orderService.seckill(user, goodsVO);
    }
}

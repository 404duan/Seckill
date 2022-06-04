package com.hut.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀消息队列
 * @author DUANQI
 * DateTime: 2022-05-29 10:45
 */
@Configuration
public class RabbitMQConfig {
    private static final String QUEUE = "secKillQueue";
    private static final String EXCHANGE = "secKillExchange";
    private static final String ROUTING_KEY = "secKill.#";

    @Bean
    public Queue queue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(topicExchange()).with(ROUTING_KEY);
    }
}

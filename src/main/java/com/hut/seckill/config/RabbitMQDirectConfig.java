package com.hut.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

/**
 * @author DUANQI
 * DateTime: 2022-05-28 15:53
 */
//@Configuration
public class RabbitMQDirectConfig {

    private static final String QUEUE01 = "queue_direct01";
    private static final String QUEUE02 = "queue_direct02";
    private static final String EXCHANGE = "directExchange";
    private static final String ROUTING_KEY01 = "queue.red";
    private static final String ROUTING_KEY02 = "queue.green";

    @Bean
    public Queue queue01(){
        return new Queue(QUEUE01);
    }

    @Bean
    public Queue queue02(){
        return new Queue(QUEUE02);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding binding01(){
        return BindingBuilder.bind(queue01()).to(directExchange()).with(ROUTING_KEY01);
    }

    @Bean
    public Binding binding02(){
        return BindingBuilder.bind(queue02()).to(directExchange()).with(ROUTING_KEY02);
    }
}

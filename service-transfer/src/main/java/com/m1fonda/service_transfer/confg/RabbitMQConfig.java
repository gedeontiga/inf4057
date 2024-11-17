package com.m1fonda.service_transfer.confg;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.m1fonda.config.*;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue transferPendingQueue(){
        return new Queue(RabbitMQConstants.TRANSFER_QUEUE, true);
    }

    @Bean
    public TopicExchange transactionExchange(){
        return new TopicExchange(RabbitMQConstants.TRANSFER_EXCHANGE);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder
            .bind(transferPendingQueue())
            .to(transactionExchange())
            .with(RabbitMQConstants.TRANSFER_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }


}

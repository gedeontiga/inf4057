package com.m1fonda.service_deposit.confg;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue depositPendingQueue(){
        return new Queue("depositPendingQueue", true);
    }

    @Bean
    public Queue depositApprovedQueue(){
        return new Queue("depositApprovedQueue", true);
    }

    @Bean
    public TopicExchange transactionExchange(){
        return new TopicExchange("transactionExchange");
    }

    @Bean
    public Binding bindingIn(){
        return BindingBuilder
            .bind(depositPendingQueue())
            .to(transactionExchange())
            .with("depositPendingQueue.routing.key");
    }

    @Bean
    public Binding bindingOut(){
        return BindingBuilder
            .bind(depositApprovedQueue())
            .to(transactionExchange())
            .with("depositApprovedQueue.routing.key");
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

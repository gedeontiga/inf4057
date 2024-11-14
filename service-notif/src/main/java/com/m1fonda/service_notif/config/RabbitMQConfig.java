package com.m1fonda.service_notif.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue notificationQueue() {
        return new Queue("notificationQueue");
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange("notificationExchange");
    }

    @Bean
    Binding bindingNotification() {
        return BindingBuilder.bind(notificationQueue()).to(notificationExchange()).with("notification.routing.key");
    }
}

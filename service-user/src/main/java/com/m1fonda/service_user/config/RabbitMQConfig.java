package com.m1fonda.service_user.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Declarer une file d'attente
    @Bean
    Queue userQueue() {
        return new Queue("userQueue", false);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("spring-boot-exchange");
    }

    // Liaison entre l’échange et la file d'attente avec une cle de routage
    @Bean
    Binding bindingUser(Queue userQueue, DirectExchange exchange) {
        return BindingBuilder.bind(userQueue).to(exchange).with("user");
    }
}

package com.m1fonda.service_demands.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue agencyQueue() {
        return new Queue("agencyQueue", true);
    }

    @Bean
    Queue demandeQueue() {
        return new Queue("demandeQueue", true);
    }

    @Bean
    DirectExchange demandeExchange() {
        return new DirectExchange("demandeExchange");
    }

    @Bean
    Binding bindingDemande() {
        return BindingBuilder.bind(demandeQueue()).to(demandeExchange()).with("demande.reject.routing.key");
    }

    Binding bindingAgency() {
        return BindingBuilder.bind(agencyQueue()).to(demandeExchange()).with("agency.routing.key");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}

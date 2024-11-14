package com.m1fonda.service_agency.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.m1fonda.config.RabbitMQConstants;

@Configuration
public class RabbitMQConfig {
    @Bean
    Queue agencyQueue() {
        return new Queue(RabbitMQConstants.AGENCY_QUEUE);
    }

    @Bean
    DirectExchange agencyExchange() {
        return new DirectExchange(RabbitMQConstants.AGENCY_EXCHANGE);
    }

    @Bean
    Binding bindingAgency() {
        return BindingBuilder.bind(agencyQueue()).to(agencyExchange()).with(RabbitMQConstants.AGENCY_KEY);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    // Important : Configurez un message listener factory pour le convertisseur JSON
    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}

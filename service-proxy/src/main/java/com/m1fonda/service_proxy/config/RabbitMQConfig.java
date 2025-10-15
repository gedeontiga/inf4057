package com.m1fonda.service_proxy.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.m1fonda.commons_libs.config.RabbitMQConstants;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue authCreationQueue() {
        return new Queue(RabbitMQConstants.MANAGER_CREATION_QUEUE);
    }

    @Bean
    Queue authDeletionQueue() {
        return new Queue(RabbitMQConstants.MANAGER_DELETION_QUEUE);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(RabbitMQConstants.AUTH_EXCHANGE);
    }

    @Bean
    Binding bindingAuthCreation() {
        return BindingBuilder.bind(authCreationQueue()).to(exchange())
                .with(RabbitMQConstants.MANAGER_CREATION_KEY);
    }

    @Bean
    Binding bindingAuthDeletion() {
        return BindingBuilder.bind(authDeletionQueue()).to(exchange())
                .with(RabbitMQConstants.MANAGER_DELETION_KEY);
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

package com.m1fonda.service_account.config;

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

import com.m1fonda.commons_libs.config.RabbitMQConstants;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue compteCreationQueue() {
        return new Queue(RabbitMQConstants.ACCOUNT_CREATION_QUEUE, false);
    }

    @Bean
    Queue compteUpdateQueue() {
        return new Queue(RabbitMQConstants.ACCOUNT_UPDATE_QUEUE, false);
    }

    @Bean
    Queue userInfoQueue() {
        return new Queue(RabbitMQConstants.USER_INFO_UPDATE_QUEUE, false);
    }

    @Bean
    DirectExchange compteExchange() {
        return new DirectExchange(RabbitMQConstants.ACCOUNT_EXCHANGE);
    }

    @Bean
    Binding bindingCompteCreation() {
        return BindingBuilder.bind(compteCreationQueue()).to(compteExchange())
                .with(RabbitMQConstants.ACCOUNT_CREATION_KEY);
    }

    @Bean
    Binding bindingCompteUpdate() {
        return BindingBuilder.bind(compteUpdateQueue()).to(compteExchange()).with(RabbitMQConstants.ACCOUNT_UPDATE_KEY);
    }

    @Bean
    Binding userInfoUpdate() {
        return BindingBuilder.bind(userInfoQueue()).to(compteExchange()).with(RabbitMQConstants.USER_INFO_UPDATE_KEY);
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

package com.m1fonda.service_account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
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
    Queue compteQueue() {
        return new Queue(RabbitMQConstants.ACCOUNT_QUEUE);
    }

    @Bean
    Queue compteCreationQueue() {
        return new Queue(RabbitMQConstants.ACCOUNT_CREATION_QUEUE);
    }

    @Bean
    Queue compteUpdateQueue() {
        return new Queue(RabbitMQConstants.ACCOUNT_UPDATE_QUEUE);
    }

    @Bean
    Queue compteTransactionQueue() {
        return new Queue(RabbitMQConstants.ACCOUNT_TRANSACTION_QUEUE);
    }

    @Bean
    TopicExchange compteExchange() {
        return new TopicExchange(RabbitMQConstants.ACCOUNT_EXCHANGE);
    }

    Binding bindingCompte() {
        return BindingBuilder.bind(compteQueue()).to(compteExchange()).with(RabbitMQConstants.ACCOUNT_KEY);
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
    Binding bindingCompteTransaction() {
        return BindingBuilder.bind(compteTransactionQueue()).to(compteExchange())
                .with(RabbitMQConstants.ACCOUNT_TRANSACTION_KEY);
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

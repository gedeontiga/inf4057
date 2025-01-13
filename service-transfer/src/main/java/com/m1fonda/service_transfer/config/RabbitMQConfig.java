package com.m1fonda.service_transfer.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.m1fonda.commons_libs.config.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    Queue transferPendingQueue() {
        return new Queue(RabbitMQConstants.TRANSFER_QUEUE, true);
    }

    @Bean
    DirectExchange transferExchange() {
        return new DirectExchange(RabbitMQConstants.TRANSFER_EXCHANGE);
    }

    @Bean
    Binding binding() {
        return BindingBuilder
                .bind(transferPendingQueue())
                .to(transferExchange())
                .with(RabbitMQConstants.TRANSFER_KEY);
    }

    @Bean
    Queue transferQueue() {
        return new Queue(RabbitMQConstants.TRANSFER_ACCOUNT_CREATION_QUEUE, false);
    }

    @Bean
    FanoutExchange transactionExchange() {
        return new FanoutExchange(RabbitMQConstants.TRANSACTION_EXCHANGE);
    }

    @Bean
    Queue accountUpdateQueue() {
        return new Queue(RabbitMQConstants.TRANSFER_ACCOUNT_UPDATE_QUEUE, false);
    }

    @Bean
    FanoutExchange transactionAccountUpdateExchange() {
        return new FanoutExchange(RabbitMQConstants.TRANSACTION_UPDATE_EXCHANGE);
    }

    @Bean
    Binding bindingAccountUpdate() {
        return BindingBuilder
                .bind(accountUpdateQueue())
                .to(transactionExchange());
    }

    @Bean
    Binding bindingTransaction() {
        return BindingBuilder
                .bind(transferQueue())
                .to(transactionExchange());
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}

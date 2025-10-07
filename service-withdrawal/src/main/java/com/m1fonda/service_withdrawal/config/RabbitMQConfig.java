package com.m1fonda.service_withdrawal.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
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
    Queue withdrawalPendingQueue() {
        return new Queue(RabbitMQConstants.WITHDRAW_QUEUE, true);
    }

    @Bean
    DirectExchange withdrawExchange() {
        return new DirectExchange(RabbitMQConstants.WITHDRAW_EXCHANGE);
    }

    @Bean
    Binding binding() {
        return BindingBuilder
                .bind(withdrawalPendingQueue())
                .to(withdrawExchange())
                .with(RabbitMQConstants.WITHDRAW_KEY);
    }

    @Bean
    Queue withdrawalQueue() {
        return new Queue(RabbitMQConstants.WITHDRAWAL_ACCOUNT_CREATION_QUEUE, false);
    }

    @Bean
    FanoutExchange transactionExchange() {
        return new FanoutExchange(RabbitMQConstants.TRANSACTION_EXCHANGE);
    }

    @Bean
    Binding bindingTransaction() {
        return BindingBuilder
                .bind(withdrawalQueue())
                .to(transactionExchange());
    }

    @Bean
    Queue accountUpdateQueue() {
        return new Queue(RabbitMQConstants.WITHDRAWAL_ACCOUNT_UPDATE_QUEUE, false);
    }

    @Bean
    FanoutExchange transactionAccountUpdateExchange() {
        return new FanoutExchange(RabbitMQConstants.TRANSACTION_UPDATE_EXCHANGE);
    }

    @Bean
    Binding bindingAccountUpdate() {
        return BindingBuilder
                .bind(accountUpdateQueue())
                .to(transactionAccountUpdateExchange());
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

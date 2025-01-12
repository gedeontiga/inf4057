package com.m1fonda.service_deposit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
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
    Queue depositPendingQueue() {
        return new Queue(RabbitMQConstants.DEPOSIT_QUEUE, true);
    }

    @Bean
    DirectExchange depositExchange() {
        return new DirectExchange(RabbitMQConstants.DEPOSIT_EXCHANGE);
    }

    @Bean
    Queue depositQueue() {
        return new Queue(RabbitMQConstants.DEPOSIT_ACCOUNT_CREATION_QUEUE, false);
    }

    @Bean
    FanoutExchange transactionExchange() {
        return new FanoutExchange(RabbitMQConstants.TRANSACTION_EXCHANGE);
    }

    @Bean
    Binding bindingTransaction() {
        return BindingBuilder
                .bind(depositQueue())
                .to(transactionExchange());
    }

    @Bean
    Binding bindingDeposit() {
        return BindingBuilder
                .bind(depositPendingQueue())
                .to(depositExchange())
                .with(RabbitMQConstants.DEPOSIT_KEY);
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

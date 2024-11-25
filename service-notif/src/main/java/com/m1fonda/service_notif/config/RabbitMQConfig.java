package com.m1fonda.service_notif.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
    Queue emailQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE);
    }

    @Bean
    Queue depositQueue() {
        return new Queue(RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_QUEUE);
    }

    @Bean
    Queue withdrawalQueue() {
        return new Queue(RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE);
    }

    @Bean
    Queue messageDepositQueue() {
        return new Queue(RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_QUEUE);
    }

    @Bean
    Queue messageWithdrawalQueue() {
        return new Queue(RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_QUEUE);
    }

    @Bean
    Queue messageTransferQueue() {
        return new Queue(RabbitMQConstants.MESSAGE_TRANSFER_NOTIFICATION_QUEUE);
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE);
    }

    @Bean
    Binding bindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_KEY);
    }

    @Bean
    Binding bindingDeposit() {
        return BindingBuilder.bind(depositQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_KEY);
    }

    @Bean
    Binding bindingWithdrawal() {
        return BindingBuilder.bind(withdrawalQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_KEY);
    }

    @Bean
    Binding bindingMessageDeposit() {
        return BindingBuilder.bind(messageDepositQueue()).to(notificationExchange())
                .with(RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY);
    }

    @Bean
    Binding bindingMessageWithdrawal() {
        return BindingBuilder.bind(messageWithdrawalQueue()).to(notificationExchange())
                .with(RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_KEY);
    }

    @Bean
    Binding bindingMessageTransfer() {
        return BindingBuilder.bind(messageTransferQueue()).to(notificationExchange())
                .with(RabbitMQConstants.MESSAGE_TRANSFER_NOTIFICATION_KEY);
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

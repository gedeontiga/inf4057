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
    Queue mailQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE, false);
    }

    @Bean
    Queue mailActivationQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_QUEUE, false);
    }

    @Bean
    Queue mailTransactionQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_TRANSACTION_QUEUE, false);
    }

    @Bean
    Queue transactionQueue() {
        return new Queue(RabbitMQConstants.NOTIFICATION_TRANSACTION_QUEUE, false);
    }

    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE);
    }

    @Bean
    Binding bindingDemandApproved() {
        return BindingBuilder.bind(mailQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_DEMAND_APPROVED_KEY);
    }

    @Bean
    Binding bindingDemandRejected() {
        return BindingBuilder.bind(mailQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_DEMAND_REJECTED_KEY);
    }

    @Bean
    Binding bindingEmail() {
        return BindingBuilder.bind(mailActivationQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_KEY);
    }

    @Bean
    Binding bindingTransaction() {
        return BindingBuilder.bind(transactionQueue()).to(notificationExchange())
                .with(RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY);
    }

    @Bean
    Binding bindingEmailTransaction() {
        return BindingBuilder.bind(mailTransactionQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_TRANSACTION_KEY);
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

package com.m1fonda.service_notif.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
<<<<<<< HEAD
=======
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
>>>>>>> origin/gedeon
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

<<<<<<< HEAD
import com.m1fonda.config.RabbitMQConstants;
=======
import com.m1fonda.commons_libs.config.RabbitMQConstants;
>>>>>>> origin/gedeon

@Configuration
public class RabbitMQConfig {

    @Bean
<<<<<<< HEAD
    Queue emailNotificationDepositQueue() {
        return new Queue(RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_QUEUE);
=======
    Queue notificationQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE);
>>>>>>> origin/gedeon
    }
    @Bean
    Queue emailNotificationWithdrawalQueue() {
        return new Queue(RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE);
    }
    @Bean
    Queue emailNotificationTransferQueue() {
        return new Queue(RabbitMQConstants.EMAIL_TRANSFER_NOTIFICATION_QUEUE);
    }
    @Bean
    Queue emailNotificationActivationQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_QUEUE);
    }
    @Bean
    Queue emailNotificationCreationQueue() {
        return new Queue(RabbitMQConstants.EMAIL_NOTIFICATION_CREATION_QUEUE);
    }
    @Bean
    Queue messageNotificationDepositQueue() {
        return new Queue(RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_QUEUE);
    }
    @Bean
    Queue messageNotificationWithdrawalQueue() {
        return new Queue(RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_QUEUE);
    }
    @Bean
    Queue messageNotificationTransferQueue() {
        return new Queue(RabbitMQConstants.MESSAGE_TRANSFER_NOTIFICATION_QUEUE);
    }


    @Bean
    TopicExchange notificationExchange() {
        return new TopicExchange(RabbitMQConstants.NOTIFICATION_EXCHANGE);
    }

    @Bean
<<<<<<< HEAD
    Binding bindingNotification1() {
        return BindingBuilder.bind(emailNotificationDepositQueue()).to(notificationExchange()).with(RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_KEY);
    }
    @Bean
    Binding bindingNotification2() {
        return BindingBuilder.bind(emailNotificationWithdrawalQueue()).to(notificationExchange()).with(RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_KEY);
    }
    @Bean
    Binding bindingNotification3() {
        return BindingBuilder.bind(emailNotificationTransferQueue()).to(notificationExchange()).with(RabbitMQConstants.EMAIL_TRANSFER_NOTIFICATION_KEY);
    }
    Binding bindingNotification4() {
        return BindingBuilder.bind(messageNotificationDepositQueue()).to(notificationExchange()).with(RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY);
    }
    @Bean
    Binding bindingNotification5() {
        return BindingBuilder.bind(messageNotificationWithdrawalQueue()).to(notificationExchange()).with(RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_KEY);
    }
    @Bean
    Binding bindingNotification6() {
        return BindingBuilder.bind(messageNotificationTransferQueue()).to(notificationExchange()).with(RabbitMQConstants.MESSAGE_TRANSFER_NOTIFICATION_KEY);
    }
    @Bean
    Binding bindingNotification7() {
        return BindingBuilder.bind(emailNotificationActivationQueue()).to(notificationExchange()).with(RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_KEY);
    }
    @Bean
    Binding bindingNotification8() {
        return BindingBuilder.bind(emailNotificationCreationQueue()).to(notificationExchange()).with(RabbitMQConstants.EMAIL_NOTIFICATION_CREATION_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
=======
    Binding bindingNotification() {
        return BindingBuilder.bind(notificationQueue()).to(notificationExchange())
                .with(RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_KEY);
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
>>>>>>> origin/gedeon
    }
}

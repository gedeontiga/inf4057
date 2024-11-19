package com.m1fonda.service_agency.config;

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
    Queue agencyQueue() {
        return new Queue(RabbitMQConstants.AGENCY_QUEUE);
    }

    @Bean
    Queue agencyCreationQueue() {
        return new Queue(RabbitMQConstants.AGENCY_CREATION_QUEUE);
    }

    @Bean
    Queue agencyDeleteQueue() {
        return new Queue(RabbitMQConstants.AGENCY_DELETE_QUEUE);
    }

    @Bean
    Queue agencyFindAllQueue() {
        return new Queue(RabbitMQConstants.AGENCY_FIND_ALL_QUEUE);
    }

    @Bean
    Queue agencyUpdateQueue() {
        return new Queue(RabbitMQConstants.AGENCY_UPDATE_QUEUE);
    }

    @Bean
    TopicExchange agencyExchange() {
        return new TopicExchange(RabbitMQConstants.AGENCY_EXCHANGE);
    }

    @Bean
    Binding bindingAgency() {
        return BindingBuilder.bind(agencyQueue()).to(agencyExchange()).with(RabbitMQConstants.AGENCY_KEY);
    }

    @Bean
    Binding bindingAgencyCreation() {
        return BindingBuilder.bind(agencyCreationQueue()).to(agencyExchange())
                .with(RabbitMQConstants.AGENCY_CREATION_KEY);
    }

    @Bean
    Binding bindingAgencyDelete() {
        return BindingBuilder.bind(agencyDeleteQueue()).to(agencyExchange()).with(RabbitMQConstants.AGENCY_DELETE_KEY);
    }

    @Bean
    Binding bindingAgencyFindAll() {
        return BindingBuilder.bind(agencyFindAllQueue()).to(agencyExchange())
                .with(RabbitMQConstants.AGENCY_FIND_ALL_KEY);
    }

    @Bean
    Binding bindingAgencyUpdate() {
        return BindingBuilder.bind(agencyUpdateQueue()).to(agencyExchange()).with(RabbitMQConstants.AGENCY_UPDATE_KEY);
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

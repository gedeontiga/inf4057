package com.m1fonda.service_bank.config;

public class RabbitMQConfig {

    // @Value("${rabbitmq.queue.name}")
    // private String bank_queue;
    // @Value("${rabbitmq.exchange.name}")
    // private String request_exchange;
    // @Value("${rabbitmq.routing.key}")
    // private String bank_routing_key;

    // @Bean
    // Queue bankQueue() {
    //     return new Queue(RabbitMQConstants, true);
    // }

    // @Bean
    // TopicExchange requestExchange() {
    //     return new TopicExchange(request_exchange);
    // }

    // @Bean
    // Binding bindingBankQueue() {
    //     return BindingBuilder.bind(bankQueue())
    //         .to(requestExchange())
    //         .with(bank_routing_key);
    // }

    // @Bean
    // RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    //     RabbitTemplate template = new RabbitTemplate(connectionFactory);
    //     template.setMessageConverter(new Jackson2JsonMessageConverter());
    //     return template;
    // }

    // @Bean
    // SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
    //     SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    //     factory.setConnectionFactory(connectionFactory);
    //     factory.setMessageConverter(new Jackson2JsonMessageConverter());
    //     return factory;
    // }
}


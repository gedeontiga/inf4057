package com.m1fonda.service_withdrawal.component;

import org.springframework.amqp.core.AmqpReplyTimeoutException;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Primary
public class CustomRabbitTemplate extends RabbitTemplate {
    
    // Constructor to maintain existing RabbitTemplate functionality
    public CustomRabbitTemplate(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }
    
    // Method with timeout for convertSendAndReceive
    public Object convertSendAndReceiveWithTimeout(String exchange, String routingKey, Object message, long timeout, TimeUnit timeUnit) {
        try {
            // Set the receive timeout for this specific call
            setReplyTimeout(timeUnit.toMillis(timeout));
            
            // Perform the send and receive operation
            return convertSendAndReceive(exchange, routingKey, message);
        } catch (AmqpReplyTimeoutException e) {
            // Handle timeout scenario
            throw new RuntimeException("Request timed out", e);
        }
    }
    
    // Alternative method with default exchange and routing key
    public Object convertSendAndReceiveWithTimeout(Object message, long timeout, TimeUnit timeUnit) {
        try {
            // Set the receive timeout for this specific call
            setReplyTimeout(timeUnit.toMillis(timeout));
            
            // Perform the send and receive operation
            return convertSendAndReceive(message);
        } catch (AmqpReplyTimeoutException e) {
            // Handle timeout scenario
            throw new RuntimeException("Request timed out", e);
        }
    }
}

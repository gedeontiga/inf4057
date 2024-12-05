package com.m1fonda.service_notif.services;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.service_notif.dto.NotificationRequest;
import com.m1fonda.service_notif.model.Notification;
import com.m1fonda.service_notif.repository.NotificationRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class MessageNotificationService {

    private static final String SEND_MAIL_FALLBACK = "sendMessageFallback";
    private static final String SERVICE_NOTIFICATION_CIRCUIT_BREAKER = "serviceMessageNotificationCircuitBreaker";
    @Autowired
    private SimpMessagingTemplate template;

    NotificationRepository notificationRepository;

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.NOTIFICATION_TRANSACTION_QUEUE)
    public void newTransaction(NotificationRequest request) {
        
        Notification notification = Notification.builder()
                                    .notificationId(request.notificationId())
                                    .agencyNum(request.agencyNum())
                                    .userEmail(request.userEmail())
                                    .message(request.message())
                                    .build();

        template.convertAndSendToUser(notification.getUserEmail(), "/topic/transactions", notification);
        template.convertAndSendToUser(notification.getAgencyNum(), "/topic/transactions", notification);

        notificationRepository.save(notification);
    }

    public void deleteNotification(String notificationId){
        notificationRepository.deleteByNotificationId(notificationId);
    }
    public void deleteUserNotification(String email){
        notificationRepository.deleteByUserEmail(email);
    }
    public void deleteAgencyNotification(String agencyNum){
        notificationRepository.deleteByAgencyNum(agencyNum);
    }


    public String getId(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10);
    }
}

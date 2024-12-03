package com.m1fonda.service_notif.services;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ActivationCodeRequest;
import com.m1fonda.commons_libs.entities.Announce;
import com.m1fonda.service_notif.model.Notification;
import com.m1fonda.service_notif.dto.NotificationRequest;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class MailNotificationService {

    private static final String CODE_ACTIVATION = "Code d'activation";
    private static final String SEND_MAIL_FALLBACK = "sendMailFallback";
    private static final String SERVICE_NOTIFICATION_CIRCUIT_BREAKER = "serviceNotificationCircuitBreaker";
    @Autowired
    private JavaMailSender javaMailSender;


    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void sendActivationCodeMail(ActivationCodeRequest activationInfo) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut %s, \n votre code d'activation est %s.\n Merci d'utiliser l'application et a bientôt.",
                activationInfo.firstName(),
                activationInfo.code());
        Announce notification = Announce.builder()
                .title(CODE_ACTIVATION)
                .description(text)
                .createAt(new Date())
                .email(activationInfo.email())
                .picture("/assets/images/bank-logo.ico")
                .build();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(activationInfo.email());
        mailMessage.setSubject(notification.getTitle());

        mailMessage.setText(notification.getDescription());
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.NOTIFICATION_TRANSACTION_QUEUE)
    public void newTransaction(NotificationRequest request) {

        Notification notification = Notification.builder()
                                    .notificationId(request.notificationId())
                                    .agencyNum(request.agencyNum())
                                    .userEmail(request.userEmail())
                                    .message(request.message())
                                    .build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(notification.getUserEmail());
        mailMessage.setText(notification.toString());
        javaMailSender.send(mailMessage);

    }


    public void sendMailFallback(Map<String, Object> infoMap, Throwable throwable) {
        System.out.println("Fallback - Mail non envoyé : " + infoMap.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }

    public String getId(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10);
    }
}

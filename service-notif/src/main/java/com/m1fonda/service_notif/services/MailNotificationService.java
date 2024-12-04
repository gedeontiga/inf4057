package com.m1fonda.service_notif.services;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ActivationCodeRequest;
import com.m1fonda.commons_libs.dto.DemandDTO;
import com.m1fonda.service_notif.dto.NotificationRequest;
import com.m1fonda.service_notif.entities.Notification;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class MailNotificationService {

    private static final String DEMANDE_TRAITÉE = "Demande traitée";
    private static final String CODE_ACTIVATION = "Code d'activation";
    private static final String SEND_MAIL_FALLBACK = "sendMailFallback";
    private static final String SERVICE_NOTIFICATION_CIRCUIT_BREAKER = "serviceNotificationCircuitBreaker";
    @Autowired
    private JavaMailSender javaMailSender;

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void sendActivationCodeMail(ActivationCodeRequest activationInfo) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut Mr. %s, \n votre code d'activation est %s.\n Merci d'utiliser l'application et a bientôt.",
                activationInfo.firstName(),
                activationInfo.code());
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(activationInfo.email());
        mailMessage.setSubject(CODE_ACTIVATION);

        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void sendApprovedDemandMail(DemandDTO demand) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut Mr. %s, \n votre demande de creation de compte a ete approuvée.\n Merci d'utiliser l'application et a bientôt.",
                demand.firstName());
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(demand.email());
        mailMessage.setSubject(DEMANDE_TRAITÉE);

        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void sendRejectedDemandMail(DemandDTO demand) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut Mr. %s, \n votre demande de creation de compte a ete rejetée car les informations sur votre CNI ne correspondent pas.\n Merci d'utiliser l'application et a bientôt.",
                demand.firstName());
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(demand.email());
        mailMessage.setSubject(DEMANDE_TRAITÉE);

        mailMessage.setText(text);
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
}
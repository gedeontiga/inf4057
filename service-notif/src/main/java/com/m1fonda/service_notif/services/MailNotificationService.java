package com.m1fonda.service_notif.services;

import java.util.Date;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.m1fonda.config.RabbitMQConstants;
import com.m1fonda.service_notif.entities.Notification;
import com.m1fonda.service_notif.entities.Users;

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
    public void sendActivationCodeMail(Users user, String codeActivation) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut %s, \n votre code d'activation est %s.\n Merci d'utiliser l'application et a bientôt.",
                user.getNom(),
                codeActivation);
        Notification notification = Notification.builder()
                .titre(CODE_ACTIVATION)
                .description(text)
                .dateEnvoi(new Date())
                .users(user)
                .pj(null)
                .build();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(notification.getTitre());

        mailMessage.setText(notification.getDescription());
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_QUEUE)
    public void sendDepositMail(Users user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        // TODO more here
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void sendWithdrawalMail(Users user) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        // TODO more here
    }

    public void sendMailFallback(Users user, Throwable throwable) {
        System.out.println("Fallback - Mail non envoyé : " + user.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}

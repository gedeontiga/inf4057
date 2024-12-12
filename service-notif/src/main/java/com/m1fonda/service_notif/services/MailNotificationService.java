package com.m1fonda.service_notif.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ActivationRequest;
import com.m1fonda.commons_libs.dto.DemandDTO;
import com.m1fonda.commons_libs.dto.NotificationRequest;
import com.m1fonda.service_notif.entities.Notification;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailNotificationService {

    private static final String DEMANDE_TRAITÉE = "Demande traitée";
    private static final String CODE_ACTIVATION = "Code d'activation";
    private static final String SEND_MAIL_FALLBACK = "sendMailFallback";
    private static final String SERVICE_NOTIFICATION_CIRCUIT_BREAKER = "serviceNotificationCircuitBreaker";
    private JavaMailSender javaMailSender;

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_QUEUE)
    public void sendActivationCodeMail(ActivationRequest request) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = "Votre code d'activation est: " + request.activationCode();
        mailMessage.setTo(request.email());
        mailMessage.setSubject(CODE_ACTIVATION);
        mailMessage.setFrom("gedeon.ambomo@facsciences-uy1.cm"); // Optionnel si Gmail gère "From"
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void sendApprovedDemandMail(DemandDTO demand) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut Mr./Mme. %s, \n votre demande de creation de compte a ete approuvée et votre compte créé.\nUn autre email vous a été envoyé pour activer votre, merci de procéder a l'activation pour utiliser l'application.",
                demand.firstName());
        mailMessage.setTo(demand.email());
        mailMessage.setSubject(DEMANDE_TRAITÉE);
        mailMessage.setFrom("gedeon.ambomo@facsciences-uy1.cm"); // Optionnel si Gmail gère "From"
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_QUEUE)
    public void sendRejectedDemandMail(DemandDTO demand) throws Exception {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String text = String.format(
                "Salut Mr./Mme. %s, \n votre demande de creation de compte a ete rejetée car les informations sur votre CNI ne correspondent pas.\n Merci d'utiliser l'application et a bientôt.",
                demand.firstName());
        mailMessage.setTo(demand.email());
        mailMessage.setSubject(DEMANDE_TRAITÉE);
        mailMessage.setFrom("gedeon.ambomo@facsciences-uy1.cm"); // Optionnel si Gmail gère "From"
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_NOTIFICATION_TRANSACTION_QUEUE)
    public void newTransaction(NotificationRequest request) {

        Notification notification = Notification.builder()
                .notificationId(request.notificationId())
                .agencyNum(request.agencyNum())
                .userEmail(request.userEmail())
                .message(request.message())
                .build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("gedeon.ambomo@facsciences-uy1.cm"); // Optionnel si Gmail gère "From"
        mailMessage.setTo(notification.getUserEmail());
        mailMessage.setText(notification.getMessage());
        javaMailSender.send(mailMessage);

    }

    public void sendMailFallback(Object infoMap, Throwable throwable) {
        System.out.println("Fallback - Mail non envoyé : " + infoMap.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}
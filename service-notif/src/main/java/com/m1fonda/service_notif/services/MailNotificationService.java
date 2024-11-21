package com.m1fonda.service_notif.services;

import java.util.Date;
import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.AccountTransferResponse;
import com.m1fonda.commons_libs.dto.ActivationCodeRequest;
import com.m1fonda.commons_libs.entities.Announce;

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
    @RabbitListener(queues = RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_QUEUE)
    public void sendDepositMail(AccountDepositWithdrawalResponse response) {
        String message = "Compte :" + response.numeroCompte() +
                "\n Utilisateur :" + response.userName() +
                "\n Montant: " + response.transactionAmount() +
                "\n Nouveau Solde: " + response.newBalance() +
                "\n Date : " + response.createdAt();
        String title = "DEPOT EFFECTUE";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(response.email());
        mailMessage.setSubject(title);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);

    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void sendWithdrawalMail(AccountDepositWithdrawalResponse response) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(response.email());
        mailMessage.setSubject("RETRAIT EFFECTUE");
        mailMessage.setText("Compte :" + response.numeroCompte() +
                "\n Utilisateur :" + response.userName() +
                "\n Montant: " + response.transactionAmount() +
                "\n Nouveau Solde: " + response.newBalance() +
                "\n Date : " + response.createdAt());
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void sendTransferMail(AccountTransferResponse response) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(response.senderEmail());
        mailMessage.setSubject("RETRAIT EFFECTUE");
        mailMessage.setText("Compte :" + response.senderAccountNumber() +
                "\n Utilisateur :" + response.senderUserName() +
                "\n Montant: " + response.transactionAmount() +
                "\n Nouveau Solde: " + response.newBalanceSender() +
                "\n Date : " + response.createdAt());
        javaMailSender.send(mailMessage);
    }

    public void sendMailFallback(Map<String, Object> infoMap, Throwable throwable) {
        System.out.println("Fallback - Mail non envoyé : " + infoMap.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}

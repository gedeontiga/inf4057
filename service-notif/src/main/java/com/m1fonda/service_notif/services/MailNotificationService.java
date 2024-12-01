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
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.ActivationCodeRequest;
import com.m1fonda.commons_libs.dto.TransferRequest;
import com.m1fonda.commons_libs.entities.Announce;
import com.m1fonda.service_notif.model.Notification;

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
        String message = "Transaction Reussie. ID: " + response.transactionID() +
                        "\n Compte :" + response.numeroCompte() +
                        "\n Client :" + response.userName() +
                        "\n Montant: " + response.transactionAmount() +
                        "\n Nouveau Solde: " + response.newBalance() +
                        "\n Date : " + response.createdAt();

        String header = "DEPOT";

        Notification notification = Notification.builder()
                                    .notificationId(getId())
                                    .agencyId(response.agencyID())
                                    .header(header)
                                    .userId1(response.email())
                                    .userId2(null)
                                    .message(message)
                                    .build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(response.email());
        mailMessage.setSubject(header);
        mailMessage.setText(notification.toString());
        javaMailSender.send(mailMessage);

    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void sendWithdrawalMail(AccountDepositWithdrawalResponse response) {
        String message = "Transaction Reussie. ID: " + response.transactionID() +
                        "\n Compte :" + response.numeroCompte() +
                        "\n Client :" + response.userName() +
                        "\n Montant: " + response.transactionAmount() +
                        "\n Nouveau Solde: " + response.newBalance() +
                        "\n Date : " + response.createdAt();

        String header = "RETRAIT";

        Notification notification = Notification.builder()
                                    .notificationId(getId())
                                    .agencyId(response.agencyID())
                                    .header(header)
                                    .userId1(response.email())
                                    .userId2(null)
                                    .message(message)
                                    .build();

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(response.email());
        mailMessage.setSubject(header);
        mailMessage.setText(notification.toString());
        javaMailSender.send(mailMessage);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void sendTransferMail(TransferRequest response) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        SimpleMailMessage mailMessage2 = new SimpleMailMessage();
        
        mailMessage.setFrom("no-reply@atg-bank.com");
        mailMessage.setTo(response.senderEmail());
        mailMessage.setSubject("TRANSFER EFFECTUE");
        mailMessage.setText("Transfer Reussi. ID: " + response.transactionNumber() +
                        "\n De :" + response.senderName() +  " - " + response.senderAccountNum() +
                        "\n À :" + response.receiverName() +  " - " + response.receiverAccountNum() +
                        "\n Montant: " + response.amount() +
                        "\n frais: " + response.fees() +
                        "\n Nouveau Solde: " + response.senderNewBalance() +
                        "\n Date : " + response.date());
        javaMailSender.send(mailMessage);

        mailMessage2.setFrom("no-reply@atg-bank.com");
        mailMessage2.setTo(response.senderEmail());
        mailMessage2.setSubject("TRANSFER EFFECTUE");
        mailMessage2.setText("Transfer Recu. ID: " + response.transactionNumber() +
                        "\n De :" + response.senderName() +  " - " + response.senderAccountNum() +
                        "\n À :" + response.receiverName() +  " - " + response.receiverAccountNum() +
                        "\n Montant: " + response.amount() +
                        "\n Nouveau Solde: " + response.receiverNewBalance() +
                        "\n Date : " + response.date());
        javaMailSender.send(mailMessage2);

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

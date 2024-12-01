package com.m1fonda.service_notif.services;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.TransferRequest;
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
    @RabbitListener(queues = RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_QUEUE)
    public void listenDeposits(AccountDepositWithdrawalResponse response) {
        String message = "Transaction Reussie. ID: " + response.transactionID() +
        "\n Compte :" + response.numeroCompte() +
        "\n Client :" + response.userName() +
        "\n Montant: " + response.transactionAmount() +
        "\n Nouveau Solde: " + response.newBalance() +
        "\n Date : " + response.createdAt();


        Notification notification = Notification.builder()
                                    .notificationId(getId())
                                    .agencyId(response.agencyID())
                                    .header("DEPOT")
                                    .userId1(response.email())
                                    .userId2(null)
                                    .message(message)
                                    .build();

        template.convertAndSendToUser(response.email(), "/topic/notifications", notification);
        template.convertAndSendToUser(response.agencyID(), "/topic/notifications", notification);

        notificationRepository.save(notification);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void listenWithdrawals(AccountDepositWithdrawalResponse response) {
        String message = "Transaction Reussie. ID: " + response.transactionID() +
        "\n Compte :" + response.numeroCompte() +
        "\n Client :" + response.userName() +
        "\n Montant: " + response.transactionAmount() +
        "\n Nouveau Solde: " + response.newBalance() +
        "\n Date : " + response.createdAt();


        Notification notification = Notification.builder()
                                    .notificationId(getId())
                                    .agencyId(response.agencyID())
                                    .header("RETRAIT")
                                    .userId1(response.email())
                                    .userId2(null)
                                    .message(message)
                                    .build();

        template.convertAndSendToUser(response.email(), "/topic/notifications", notification);
        template.convertAndSendToUser(response.agencyID(), "/topic/notifications", notification);

        notificationRepository.save(notification);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.MESSAGE_TRANSFER_NOTIFICATION_QUEUE)
    public void listenTransfers(TransferRequest response) {

        String message1 = "Transfer Reussi. ID: " + response.transactionNumber() +
                        "\n De :" + response.senderName() +  " - " + response.senderAccountNum() +
                        "\n À :" + response.receiverName() +  " - " + response.receiverAccountNum() +
                        "\n Montant: " + response.amount() +
                        "\n frais: " + response.fees() +
                        "\n Nouveau Solde: " + response.senderNewBalance() +
                        "\n Date : " + response.date();

        String message2 = "Transfer Recu. ID: " + response.transactionNumber() +
                        "\n De :" + response.senderName() +  " - " + response.senderAccountNum() +
                        "\n À :" + response.receiverName() +  " - " + response.receiverAccountNum() +
                        "\n Montant: " + response.amount() +
                        "\n Nouveau Solde: " + response.receiverNewBalance() +
                        "\n Date : " + response.date();

        Notification notification1 = Notification.builder()
                                    .notificationId(getId())
                                    .agencyId(response.senderAgency())
                                    .header("TRANSFER")
                                    .userId1(response.senderEmail())
                                    .userId2(response.receiverEmail())
                                    .message(message1)
                                    .build();

        Notification notification2 = Notification.builder()
                                    .notificationId(getId())
                                    .agencyId(response.senderAgency())
                                    .header("TRANSFER")
                                    .userId1(response.senderEmail())
                                    .userId2(response.receiverEmail())
                                    .message(message2)
                                    .build();

        template.convertAndSendToUser(response.senderEmail(), "/topic/notifications", notification1);
        template.convertAndSendToUser(response.receiverEmail(), "/topic/notifications", notification2);

        notificationRepository.save(notification1);
    }

    public String getId(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10);
    }
}

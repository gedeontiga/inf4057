package com.m1fonda.service_notif.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.AccountTransferResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class MessageNotificationService {

    private static final String SEND_MAIL_FALLBACK = "sendMessageFallback";
    private static final String SERVICE_NOTIFICATION_CIRCUIT_BREAKER = "serviceMessageNotificationCircuitBreaker";
    @Autowired
    private SimpMessagingTemplate template;

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_QUEUE)
    public void listenDeposits(AccountDepositWithdrawalResponse response) {

        String message = "Dépôt Effectué :" +
                "\n Compte :" + response.numeroCompte() +
                "\n Utilisateur :" + response.userName() +
                "\n Montant: " + response.transactionAmount() +
                "\n Nouveau Solde: " + response.newBalance() +
                "\n Date : " + response.createdAt();
        template.convertAndSend("/topic/notifications", message);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_QUEUE)
    public void listenWithdrawals(AccountDepositWithdrawalResponse response) {
        String message = "Retrait Effectué :" +
                "\n Compte :" + response.numeroCompte() +
                "\n Utilisateur :" + response.userName() +
                "\n Montant: " + response.transactionAmount() +
                "\n Nouveau Solde: " + response.newBalance() +
                "\n Date : " + response.createdAt();
        template.convertAndSend("/topic/notifications", message);
    }

    @CircuitBreaker(name = SERVICE_NOTIFICATION_CIRCUIT_BREAKER, fallbackMethod = SEND_MAIL_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.MESSAGE_TRANSFER_NOTIFICATION_QUEUE)
    public void listenTransfers(AccountTransferResponse response) {
        String message = "Transfer Effectué :" +
                "\n Vers le Compte :" + response.receiverAccountNumber() +
                "\n Nom :" + response.receiverUserName() +
                "\n Montant: " + response.transactionAmount() +
                "\n Nouveau Solde: " + response.newBalanceSender() +
                "\n Date : " + response.createdAt();
        template.convertAndSend("/topic/notifications", message);
    }
}

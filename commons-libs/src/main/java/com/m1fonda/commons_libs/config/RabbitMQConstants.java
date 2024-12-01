package com.m1fonda.commons_libs.config;

public final class RabbitMQConstants {
    // Exchanges
    public static final String ACCOUNT_EXCHANGE = "bank.account.exchange";
    public static final String AGENCY_EXCHANGE = "bank.agency.exchange";
    public static final String USER_EXCHANGE = "bank.user.exchange";
    public static final String NOTIFICATION_EXCHANGE = "bank.notification.exchange";
    public static final String AUTH_EXCHANGE = "bank.auth.exchange";

    // Account Queues & Routing Keys
    public static final String ACCOUNT_CREATION_QUEUE = "account.creation.queue";
    public static final String ACCOUNT_UPDATE_QUEUE = "account.update.queue";
    public static final String ACCOUNT_CREATION_KEY = "account.creation.key";
    public static final String ACCOUNT_UPDATE_KEY = "account.update.key";

    public static final String ACCOUNT_DEPOSIT_QUEUE = "account.deposit.queue";
    public static final String ACCOUNT_DEPOSIT_KEY = "account.deposit.key";
    public static final String ACCOUNT_WITHDRAW_QUEUE = "account.withdraw.queue";
    public static final String ACCOUNT_WITHDRAW_KEY = "account.withdraw.key";
    public static final String ACCOUNT_TRANSFER_QUEUE = "account.transfer.queue";
    public static final String ACCOUNT_TRANSFER_KEY = "account.transfer.key";
    
    // Agency Queues & Routing Keys
    public static final String AGENCY_QUEUE = "agency.queue";
    public static final String AGENCY_UPDATE_QUEUE = "agency.update.queue";
    public static final String AGENCY_CREATION_QUEUE = "agency.creation.queue";
    public static final String AGENCY_DELETE_QUEUE = "agency.delete.queue";
    public static final String AGENCY_FIND_ALL_QUEUE = "agency.find.all.queue";
    public static final String AGENCY_KEY = "agency.key";
    public static final String AGENCY_UPDATE_KEY = "agency.update.key";
    public static final String AGENCY_CREATION_KEY = "agency.creation.key";
    public static final String AGENCY_DELETE_KEY = "agency.delete.key";
    public static final String AGENCY_FIND_ALL_KEY = "agency.find.all.key";

    // User Queues & Routing Keys
    public static final String USER_CREATION_QUEUE = "user.creation.queue";
    public static final String USER_UPDATE_QUEUE = "user.update.queue";
    public static final String USER_CREATION_KEY = "user.creation.key";
    public static final String USER_UPDATE_KEY = "user.update.key";

    // Authentication Queues & Routing Keys
    public static final String AUTH_REGISTER_QUEUE = "user.register.queue";
    public static final String AUTH_REGISTER_KEY = "user.register.key";

    // Notification Queues & Routing Keys
    public static final String EMAIL_NOTIFICATION_QUEUE = "notification.email.queue";
    public static final String EMAIL_NOTIFICATION_ACTIVATION_KEY = "notification.email.activation.key";
    public static final String EMAIL_NOTIFICATION_CREATION_KEY = "notification.email.creation.key";
    public static final String EMAIL_DEPOSIT_NOTIFICATION_QUEUE = "notification.email.deposit.queue";
    public static final String EMAIL_DEPOSIT_NOTIFICATION_KEY = "notification.email.deposit.key";
    public static final String EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE = "notification.email.withdrawal.queue";
    public static final String EMAIL_WITHDRAWAL_NOTIFICATION_KEY = "notification.email.withdrawal.key";
    public static final String EMAIL_TRANSFER_NOTIFICATION_QUEUE = "notification.email.transfer.queue";
    public static final String EMAIL_TRANSFER_NOTIFICATION_KEY = "notification.email.transfer.key";

    public static final String MESSAGE_DEPOSIT_NOTIFICATION_QUEUE = "message.deposit.email.queue";
    public static final String MESSAGE_DEPOSIT_NOTIFICATION_KEY = "message.email.deposit.key";
    public static final String MESSAGE_WITHDRAWAL_NOTIFICATION_QUEUE = "message.email.withdrawal.queue";
    public static final String MESSAGE_WITHDRAWAL_NOTIFICATION_KEY = "message.email.withdrawal.key";
    public static final String MESSAGE_TRANSFER_NOTIFICATION_QUEUE = "message.email.transfer.queue";
    public static final String MESSAGE_TRANSFER_NOTIFICATION_KEY = "message.email.transfer.key";

    // Deposit, Withdrawal and Transfer queues & routing keys

    public static final String DEPOSIT_QUEUE = "depositQueue";
    public static final String DEPOSIT_EXCHANGE = "depositExchange";
    public static final String DEPOSIT_KEY = "depositKey";
    public static final String WITHDRAW_EXCHANGE = "withdrawExchange";
    public static final String WITHDRAW_QUEUE = "withdrawQueue";
    public static final String WITHDRAW_KEY = "withdrawKey";
    public static final String TRANSFER_EXCHANGE = "transferExchange";
    public static final String TRANSFER_QUEUE = "transferQueue";
    public static final String TRANSFER_KEY = "transferKey";

    public static final String AGENCY_DEPOSIT_KEY = "agency.deposit.key";
    public static final String AGENCY_DEPOSIT_QUEUE = "agency.deposit.queue";

    public static final String AGENCY_WITHDRAW_KEY = "agency.withdraw.key";
    public static final String AGENCY_WITHDRAW_QUEUE = "agency.withdraw.queue";

    public static final String AGENCY_TRANSFER_KEY = "agency.transfer.key";
    public static final String AGENCY_TRANSFER_QUEUE = "agency.transfer.queue";

    public static final String USER_READ_KEY = "user.read.key";
    public static final String USER_READ_QUEUE = "user.read.queue";

    public static final String ACCOUNT_READ_KEY = "account.read.key";
    public static final String ACCOUNT_READ_QUEUE = "account.read.queue";

    public static final String AGENCY_READ_KEY = "agency.read.key";
    public static final String AGENCY_READ_QUEUE = "agency.read.queue";
}

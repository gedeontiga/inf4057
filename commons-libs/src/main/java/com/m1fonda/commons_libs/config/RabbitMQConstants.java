package com.m1fonda.commons_libs.config;

public final class RabbitMQConstants {
    // Exchanges
    public static final String BANK_EXCHANGE = "bank.exchange";
    public static final String ACCOUNT_EXCHANGE = "bank.account.exchange";
    public static final String AGENCY_EXCHANGE = "bank.agency.exchange";
    public static final String USER_EXCHANGE = "bank.user.exchange";
    public static final String NOTIFICATION_EXCHANGE = "bank.notification.exchange";
    public static final String AUTH_EXCHANGE = "bank.auth.exchange";
    public static final String TRANSACTION_EXCHANGE = "bank.transaction.exchange";
    public static final String TRANSACTION_UPDATE_EXCHANGE = "bank.transaction.update.exchange";

    // Bank Queues & Routing Keys
    public static final String BANK_QUEUE = "bank.queue";
    public static final String BANK_KEY = "bank.key";

    // Account Queues & Routing Keys
    public static final String ACCOUNT_QUEUE = "account.queue";
    public static final String ACCOUNT_KEY = "account.key";
    public static final String DEPOSIT_ACCOUNT_CREATION_QUEUE = "deposit.account.creation.queue";
    public static final String WITHDRAWAL_ACCOUNT_CREATION_QUEUE = "withdrawal.account.creation.queue";
    public static final String TRANSFER_ACCOUNT_CREATION_QUEUE = "transfer.account.creation.queue";
    public static final String DEPOSIT_ACCOUNT_UPDATE_QUEUE = "deposit.account.update.queue";
    public static final String WITHDRAWAL_ACCOUNT_UPDATE_QUEUE = "withdrawal.account.update.queue";
    public static final String TRANSFER_ACCOUNT_UPDATE_QUEUE = "transfer.account.update.queue";
    public static final String TRANSACTION_ACCOUNT_CREATION_KEY = "transaction.account.creation.key";
    public static final String ACCOUNT_CREATION_QUEUE = "account.creation.queue";
    public static final String ACCOUNT_CREATION_KEY = "account.creation.key";
    public static final String ACCOUNT_UPDATE_QUEUE = "account.update.queue";
    public static final String ACCOUNT_UPDATE_KEY = "account.update.key";
    public static final String ACCOUNT_TRANSACTION_QUEUE = "account.transaction.queue";
    public static final String ACCOUNT_TRANSACTION_KEY = "account.transaction.key";

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
    public static final String AUTH_USER_CREATION_QUEUE = "auth.user.creation.queue";
    public static final String AUTH_USER_CREATION_KEY = "auth.user.creation.key";
    public static final String AUTH_USER_DELETION_QUEUE = "auth.user.deletion.queue";
    public static final String AUTH_USER_DELETION_KEY = "auth.user.deletion.key";
    public static final String MANAGER_DELETION_QUEUE = "manager.deletion.queue";
    public static final String MANAGER_CREATION_QUEUE = "manager.creation.queue";
    public static final String MANAGER_DELETION_KEY = "manager.deletion.key";
    public static final String MANAGER_CREATION_KEY = "manager.creation.key";
    public static final String AUTH_REGISTER_QUEUE = "user.register.queue";
    public static final String AUTH_REGISTER_KEY = "user.register.key";

    // Notification Queues & Routing Keys
    public static final String USER_MAIL_NOTIFICATION_QUEUE = "user.mail.notification.queue";
    public static final String USER_MAIL_NOTIFICATION_KEY = "user.mail.notification.key";
    public static final String DEMAND_MAIL_NOTIFICATION_QUEUE = "demand.mail.notification.queue";
    public static final String DEMAND_MAIL_NOTIFICATION_KEY = "notification.email.demand.key";
    public static final String EMAIL_NOTIFICATION_ACTIVATION_QUEUE = "notification.email.activation.queue";
    public static final String EMAIL_NOTIFICATION_TRANSACTION_QUEUE = "notification.transaction.queue";
    public static final String EMAIL_NOTIFICATION_ACTIVATION_KEY = "notification.email.activation.key";
    public static final String EMAIL_NOTIFICATION_TRANSACTION_KEY = "notification.transaction.key";
    // public static final String EMAIL_NOTIFICATION_CREATION_KEY =
    // "notification.email.creation.key";
    // public static final String EMAIL_DEPOSIT_NOTIFICATION_QUEUE =
    // "notification.email.queue";
    // public static final String EMAIL_DEPOSIT_NOTIFICATION_KEY =
    // "notification.email.deposit.key";
    // public static final String EMAIL_WITHDRAWAL_NOTIFICATION_QUEUE =
    // "notification.email.queue";
    // public static final String EMAIL_WITHDRAWAL_NOTIFICATION_KEY =
    // "notification.email.withdrawal.key";

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
    public static final String NOTIFICATION_TRANSACTION_QUEUE = "notification.transaction.queue";
    public static final String NOTIFICATION_TRANSACTION_KEY = "notification.transaction.key";
}

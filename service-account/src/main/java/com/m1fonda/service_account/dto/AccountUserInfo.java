
package com.m1fonda.service_account.dto;

import com.m1fonda.service_account.entities.Compte;
import com.m1fonda.service_account.entities.Users;

public record AccountUserInfo(
        String numAccount,
        String numAgency,
        Double balance,
        String status,
        String email,
        String firstName,
        String lastName,
        Long phoneNumber) {
    public static AccountUserInfo fromUser(Users user, Compte account) {
        return new AccountUserInfo(
                account.getNumAccount(),
                account.getNumAgency(),
                account.getBalance(),
                account.getStatus().name(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber());
    }
}
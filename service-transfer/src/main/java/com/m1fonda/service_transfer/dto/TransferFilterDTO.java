package com.m1fonda.service_transfer.dto;

import lombok.Data;
import org.springframework.data.mongodb.core.query.Criteria;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransferFilterDTO {
    private String email;
    private String accountId;
    private String agencyId;
    private String bankId;

    public Criteria buildCriteria() {
        List<Criteria> criteriaList = new ArrayList<>();

        if (email != null && !email.isEmpty()) {
            criteriaList.add(Criteria.where("users.users_email").is(email));
        }
        if (accountId != null && !accountId.isEmpty()) {
            criteriaList.add(Criteria.where("comptes.numAccount").is(accountId));
        }
        if (agencyId != null && !agencyId.isEmpty()) {
            criteriaList.add(Criteria.where("agences.num_agency").is(agencyId));
        }
        if (bankId != null && !bankId.isEmpty()) {
            criteriaList.add(Criteria.where("banks.bankNumber").is(bankId));
        }

        if (criteriaList.isEmpty()) {
            throw new IllegalArgumentException("At least one filter parameter must be provided");
        }

        return new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
    }
}
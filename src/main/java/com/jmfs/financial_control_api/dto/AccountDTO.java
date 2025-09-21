package com.jmfs.financial_control_api.dto;

import com.jmfs.financial_control_api.entity.Account;

import java.math.BigDecimal;

public record AccountDTO(Long userId, String name, String type, String currency, BigDecimal balance_snapshot, String institution) {
    public static AccountDTO fromEntity(Account account) {
        return new AccountDTO(account.getUser().getId(), account.getName(),
                                account.getType().getValue(), account.getCurrency(),
                                account.getBalance_snapshot(), account.getInstitution()
                            );
    }
}

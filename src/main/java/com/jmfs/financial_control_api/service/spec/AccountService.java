package com.jmfs.financial_control_api.service.spec;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.entity.Account;

public interface AccountService {
    Account createAccount(String token, AccountDTO accountDTO);

    Account getAllAccountsByUser(AccountDTO accountDTO);

    Account getAccount(AccountDTO accountDTO);
}

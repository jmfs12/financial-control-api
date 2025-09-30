package com.jmfs.financial_control_api.service.spec;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    void createAccount(String token, AccountDTO accountDTO);

    Page<AccountDTO> getAllAccountsByUser(String token, Pageable pageable) ;

    AccountDTO getAccount(String token, String name);

    void deleteAccount(String token, String name);

    void updateAccount(String token, AccountDTO accountDTO);
}

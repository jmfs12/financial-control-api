package com.jmfs.financial_control_api.service.spec;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.dto.UserDTO;
import com.jmfs.financial_control_api.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AccountService {
    Account createAccount(String token, AccountDTO accountDTO);

    public Page<Account> getAllAccountsByUser(String token, Long userId, Pageable pageable) ;

    Account getAccount(String token, AccountDTO accountDTO);
}

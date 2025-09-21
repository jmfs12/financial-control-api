package com.jmfs.financial_control_api.service.impl;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.dto.TokenClaimsDTO;
import com.jmfs.financial_control_api.entity.Account;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.TypeEnum;
import com.jmfs.financial_control_api.exceptions.AccessDeniedException;
import com.jmfs.financial_control_api.exceptions.AccountAlreadyExists;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.repository.AccountRepository;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.AccountService;
import com.jmfs.financial_control_api.service.spec.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public Account createAccount(String token, AccountDTO accountDTO) {
        TokenClaimsDTO claim = tokenService.extractClaim(token);
        if (!claim.id().equals(accountDTO.user_id()) && !claim.role().equals(RoleEnum.ADMIN.getValue())) {
            throw new AccessDeniedException("Invalid request for creating account");
        }

        log.debug("[ACCOUNT SERVICE] Creating account for {}", accountDTO);
        User user = userRepository.findById(accountDTO.user_id())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + accountDTO.user_id()));

        if (accountRepository.existsByNameAndUserId(accountDTO.name(), user.getId())) {
            throw new AccountAlreadyExists("Account with name " + accountDTO.name() + "already exists");
        }

        Account account = Account.builder()
                .user(user)
                .name(accountDTO.name())
                .currency(accountDTO.currency())
                .type(TypeEnum.fromString(accountDTO.type()))
                .balance_snapshot(accountDTO.balance_snapshot())
                .institution(accountDTO.institution())
                .build();

        return accountRepository.save(account);
    }

    @Override
    public Account getAllAccountsByUser(AccountDTO accountDTO) {
        return null;
    }

    @Override
    public Account getAccount(AccountDTO accountDTO){
        return null;
    }
}

package com.jmfs.financial_control_api.service.impl;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.entity.Account;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.TypeEnum;
import com.jmfs.financial_control_api.exceptions.AccessDeniedException;
import com.jmfs.financial_control_api.exceptions.AccountAlreadyExists;
import com.jmfs.financial_control_api.exceptions.AccountNotFoundException;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.repository.AccountRepository;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.AccountService;
import com.jmfs.financial_control_api.service.spec.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public Account createAccount(String token, AccountDTO accountDTO) {
        verifyRequester(token, accountDTO.userId());

        log.debug("[ACCOUNT SERVICE] Creating account for {}", accountDTO);
        User user = userRepository.findById(accountDTO.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + accountDTO.userId()));

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
    public Page<Account> getAllAccountsByUser(String token, Long userId, Pageable pageable) {
        verifyRequester(token, userId);

        Specification<Account> spec =
                (root, query, cb) ->
                        cb.equal(root.get("user").get("id"), userId);
        return accountRepository.findAll(spec, pageable);
    }

    @Override
    public Account getAccount(String token, AccountDTO accountDTO){
        verifyRequester(token, accountDTO.userId());

        Specification<Account> spec =
                (root, query, cb) ->
                        cb.and(
                                cb.equal(root.get("user").get("id"), accountDTO.userId()),
                                cb.equal(root.get("name"), accountDTO.name())
                        );

        return accountRepository.findOne(spec).
                orElseThrow(() -> new AccountNotFoundException("Account not found with name: " + accountDTO.name()));
    }

    private void verifyRequester(String token, Long userId){
        if (!tokenService.extractClaim(token).id().equals(userId)) {
            throw new AccessDeniedException("Invalid request for getting account");
        }
    }
}

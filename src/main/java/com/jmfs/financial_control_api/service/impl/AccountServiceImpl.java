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

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public void createAccount(String token, AccountDTO accountDTO) {
        log.debug("[ACCOUNT SERVICE] Creating account for {}", accountDTO);
        verifyRequester(token, accountDTO.userId());

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

        accountRepository.save(account);
    }

    @Override
    public Page<AccountDTO> getAllAccountsByUser(String token, Pageable pageable) {
        log.debug("[ACCOUNT SERVICE] Getting all accounts in page {}", pageable);
        Long userId = tokenService.extractClaim(token).id();

        Specification<Account> spec =
                (root, query, cb) ->
                        cb.equal(root.get("user").get("id"), userId);
        return accountRepository.findAll(spec, pageable)
                .map(AccountDTO::fromEntity);

    }

    @Override
    public AccountDTO getAccount(String token, AccountDTO accountDTO){
        log.debug("[ACCOUNT SERVICE] Getting {} account", accountDTO.name());
        Account account = findAccount(token, accountDTO.userId(), accountDTO.name());
        return AccountDTO.fromEntity(account);
    }

    @Override
    public void deleteAccount(String token, AccountDTO accountDTO) {
        log.debug("[ACCOUNT SERVICE] Deleting {} account", accountDTO.name());
        Account account = findAccount(token, accountDTO.userId(), accountDTO.name());

        accountRepository.delete(account);
    }

    @Override
    public void updateAccount(String token, AccountDTO accountDTO){
        log.debug("[ACCOUNT SERVICE] Updating {} account", accountDTO.name());
        Account account = findAccount(token, accountDTO.userId(), accountDTO.name());

        if (accountDTO.type() != null){
            account.setType(TypeEnum.fromString(accountDTO.type()));
        }
        if (accountDTO.balance_snapshot() != null){
            account.setBalance_snapshot(accountDTO.balance_snapshot());
        }
        if (accountDTO.institution() != null){
            account.setInstitution(accountDTO.institution());
        }
        accountRepository.save(account);
    }

    private Account findAccount(String token, Long userId, String name){
        verifyRequester(token, userId);

        Specification<Account> spec =
                (root, query, cb) ->
                        cb.and(
                                cb.equal(root.get("user").get("id"), userId),
                                cb.equal(root.get("name"), name)
                        );

        return accountRepository.findOne(spec).
                orElseThrow(() -> new AccountNotFoundException("Account not found with name: " + name));
    }

    private void verifyRequester(String token, Long userId){
        log.debug("[ACCOUNT SERVICE] verifying request");
        if (!tokenService.extractClaim(token).id().equals(userId)) {
            throw new AccessDeniedException("Invalid request: requester is trying to access other user info ");
        }
    }

}

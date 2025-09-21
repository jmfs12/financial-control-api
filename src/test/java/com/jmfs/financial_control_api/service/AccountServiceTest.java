package com.jmfs.financial_control_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.jmfs.financial_control_api.dto.AccountDTO;
import com.jmfs.financial_control_api.dto.TokenClaimsDTO;
import com.jmfs.financial_control_api.entity.Account;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import com.jmfs.financial_control_api.entity.enums.TypeEnum;
import com.jmfs.financial_control_api.repository.AccountRepository;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.impl.AccountServiceImpl;
import com.jmfs.financial_control_api.service.spec.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AccountServiceImpl accountService;

    Account testAccount1;
    Account testAccount2;
    Account testAccount3;
    User testUser1;
    User testUser2;

    TokenClaimsDTO testClaims;
    AccountDTO testAccountDTO;

    @BeforeEach
    void setUp() {

        testUser1 = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@example.com")
                .password("password123")
                .role(RoleEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();

        testUser2 = User.builder()
                .id(2L)
                .name("Maria Santos")
                .email("maria@example.com")
                .password("password456")
                .role(RoleEnum.ADMIN)
                .status(StatusEnum.ACTIVE)
                .build();

        testAccount1 = Account.builder()
                .id(1L)
                .name("Test Account 1")
                .type(TypeEnum.CREDIT)
                .currency("BRL")
                .institution("Institution")
                .balance_snapshot(BigDecimal.TWO)
                .user(testUser1)
                .build();

        testAccount2 = Account.builder()
                .id(1L)
                .name("Test Account 2")
                .type(TypeEnum.SAVINGS)
                .currency("BRL")
                .institution("Institution")
                .balance_snapshot(BigDecimal.TWO)
                .user(testUser1)
                .build();

        testAccount3 = Account.builder()
                .id(1L)
                .name("Test Account 3")
                .type(TypeEnum.CREDIT)
                .currency("BRL")
                .institution("Institution")
                .balance_snapshot(BigDecimal.TWO)
                .user(testUser2)
                .build();

        testClaims = new TokenClaimsDTO(1L, "USER");
        testAccountDTO = AccountDTO.fromEntity(testAccount1);
    }

    @Test
    void createAccount() {

        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        when(accountRepository.existsByNameAndUserId(testAccount1.getName(), testAccount1.getId())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(testAccount1.getId());
            return account;
        });

        Account newAccount = accountService.createAccount("token", testAccountDTO);

        assertNotNull(newAccount);
        assertEquals(testAccount1.getId(), newAccount.getId());
        assertEquals(testAccount1.getName(), newAccount.getName());

        verify(accountRepository, times(1)).save(any(Account.class));

    }

    @Test
    void getAllAccountsByUser() {
    }

    @Test
    void getAccount() {
    }

    @Test
    void deleteAccount() {
    }

    @Test
    void updateAccount() {
    }
}
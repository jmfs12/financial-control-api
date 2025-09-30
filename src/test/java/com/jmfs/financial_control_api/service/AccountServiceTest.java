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
import com.jmfs.financial_control_api.exceptions.AccessDeniedException;
import com.jmfs.financial_control_api.exceptions.AccountNotFoundException;
import com.jmfs.financial_control_api.repository.AccountRepository;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.impl.AccountServiceImpl;
import com.jmfs.financial_control_api.service.spec.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
    String token = "any-token";

    Pageable pageable;

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

        testClaims = TokenClaimsDTO.fromEntity(testUser1);
        testAccountDTO = AccountDTO.fromEntity(testAccount1);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Create account successfully when all is ok")
    void createAccountCase1() {

        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(userRepository.findById(testUser1.getId())).thenReturn(Optional.of(testUser1));
        when(accountRepository.existsByNameAndUserId(testAccount1.getName(), testAccount1.getId())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(testAccount1.getId());
            return account;
        });

        accountService.createAccount(token, testAccountDTO);

        verify(accountRepository, times(1)).save(any(Account.class));

    }

    @Test
    @DisplayName("Throw exception when requester try to create account for other user")
    void createAccountCase2(){
        testClaims = TokenClaimsDTO.fromEntity(testUser2);
        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        assertThrows(
                AccessDeniedException.class,
                () -> accountService.createAccount(token, testAccountDTO)
        );
    }

    @Test
    @DisplayName("Should return accounts by page for user")
    void getAllAccountsByUserCase1() {
        List<Account> accounts = Arrays.asList(testAccount1, testAccount2);
        Page<Account> expectedPage = new PageImpl<>(accounts, pageable, accounts.size());

        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(accountRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expectedPage);

        Page<AccountDTO> resultPage = accountService.getAllAccountsByUser(token, pageable);

        assertEquals(expectedPage.getTotalElements(), resultPage.getTotalElements(), "Numbers of result should be equal");
        assertEquals(expectedPage
                .getContent().getFirst().getUser().getId(),
                resultPage.getContent().getFirst().userId(),
                "User id should be equal for first account");
        assertEquals(expectedPage
                .getContent().get(1).getUser().getId(),
                resultPage.getContent().getFirst().userId(),
                "User id should be equal for second account");

        verify(accountRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Should return account successfully")
    void getAccountCase1() {
        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(accountRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(testAccount1));

        AccountDTO acc = accountService.getAccount(token, testAccountDTO);
        assertNotNull(acc);
        assertEquals(testAccount1.getName(), acc.name(), "Account name should be equal");

        verify(accountRepository).findOne(any(Specification.class));
    }

    @Test
    @DisplayName("Should throw exception when not find account")
    void getAccountCase2() {
        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(accountRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccount(token, testAccountDTO)
        );
    }

    @Test
    @DisplayName("Should delete account successfully")
    void deleteAccount() {
        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(accountRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(testAccount1));
        accountService.deleteAccount(token, testAccountDTO);
        verify(accountRepository, times(1)).delete(any(Account.class));
    }

    @Test
    @DisplayName("Should update account successfully")
    void updateAccount() {
        when(tokenService.extractClaim(anyString())).thenReturn(testClaims);
        when(accountRepository.findOne(any(Specification.class)))
                .thenReturn(Optional.of(testAccount2));
        accountService.updateAccount(token, testAccountDTO);
        assertEquals(testAccount2.getType().getValue(), testAccountDTO.type());
        assertEquals(testAccount2.getBalance_snapshot(), testAccountDTO.balance_snapshot());
        assertEquals(testAccount2.getInstitution(), testAccountDTO.institution());
        verify(accountRepository, times(1)).save(any(Account.class));

    }
}
package com.jmfs.financial_control_api.repository;

import com.jmfs.financial_control_api.entity.Account;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import com.jmfs.financial_control_api.entity.enums.TypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class AccountRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;

    @BeforeEach()
    void setUp() {
        User testAdmin = User.builder()
                .name("admin")
                .email("admin@gmail.com")
                .password("adminpassword")
                .role(RoleEnum.ADMIN)
                .status(StatusEnum.ACTIVE)
                .build();
        entityManager.persistAndFlush(testAdmin);

        testAccount = Account.builder()
                .name("Test Account")
                .user(testAdmin)
                .institution("Institution")
                .balance_snapshot(BigDecimal.TEN)
                .currency("BRL")
                .type(TypeEnum.CREDIT)
                .build();

        entityManager.persistAndFlush(testAccount);
    }

    @Test
    @DisplayName("Should return true when an account exists with name and userId")
    void existsByNameAndUserIdCase1() {
        assertTrue(accountRepository.existsByNameAndUserId(testAccount.getName(), testAccount.getId()));
    }

    @Test
    @DisplayName("Should return false when id is wrong")
    void existsByNameAndUserIdCase2(){
        assertFalse(accountRepository.existsByNameAndUserId(testAccount.getName(), 8L));
    }
}
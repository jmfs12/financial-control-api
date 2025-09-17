package com.jmfs.financial_control_api.repository;

import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
 
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private User testAdmin;


    @BeforeEach()
    void setUp() {
        testUser = User.builder()
                .name("user")
                .email("user@gmail.com")
                .password("userpassword")
                .role(RoleEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();

        testAdmin = User.builder()
                .name("admin")
                .email("admin@gmail.com")
                .password("adminpassword")
                .role(RoleEnum.ADMIN)
                .status(StatusEnum.ACTIVE)
                .build();

        entityManager.persistAndFlush(testUser);
        entityManager.persistAndFlush(testAdmin);
    }

    @Test
    @DisplayName("Should delete all users with id in list")
    void deleteByIds_case1(){
        userRepository.deleteByIds(List.of(testUser.getId(), testAdmin.getId()));
        entityManager.flush();

        Optional<User> user1 = userRepository.findById(testUser.getId());
        Optional<User> user2 = userRepository.findById(testAdmin.getId());

        assertTrue(user1.isEmpty());
        assertTrue(user2.isEmpty());
    }

    @Test
    @DisplayName("Should delete just the user with id in list")
    void deleteByIds_case2(){
        userRepository.deleteByIds(List.of(testAdmin.getId()));
        entityManager.flush();

        Optional<User> user1 = userRepository.findById(testAdmin.getId());
        Optional<User> user2 = userRepository.findById(testUser.getId());

        assertTrue(user1.isEmpty());
        assertTrue(user2.isPresent());
    }

    @Test
    @DisplayName("Should return true when a user is Admin and false when not")
    void isAdminTest(){
        assertTrue(userRepository.isAdmin(testAdmin.getId(), RoleEnum.ADMIN));
        assertFalse(userRepository.isAdmin(testUser.getId(), RoleEnum.ADMIN));
    }

}

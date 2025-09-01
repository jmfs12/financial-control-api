package com.jmfs.financial_control_api.repository;

import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
 
// apenas exemplo... não é necessário testar no momento
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
    private IUserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setStatus(StatusEnum.ACTIVE);
        testUser.setRole(RoleEnum.USER);
        testUser.setCreatedAt(Instant.now());
    }

    @Test
    void testSaveUser() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        assertEquals(testUser.getPassword(), savedUser.getPassword());
        assertEquals(testUser.getStatus(), savedUser.getStatus());
        assertEquals(testUser.getRole(), savedUser.getRole());
        assertNotNull(savedUser.getCreatedAt());
    }

    @Test
    void testFindByEmail_UserExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        assertEquals(testUser.getPassword(), foundUser.get().getPassword());
        assertEquals(testUser.getStatus(), foundUser.get().getStatus());
        assertEquals(testUser.getRole(), foundUser.get().getRole());
    }

    @Test
    void testFindByEmail_UserNotExists() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByEmail_CaseInsensitive() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByEmail("TEST@EXAMPLE.COM");

        // Then
        // Note: This depends on database collation. PostgreSQL is case-sensitive by default
        // If you need case-insensitive search, you'd need to modify the repository method
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindById() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
    }

    @Test
    void testDeleteUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        Long userId = savedUser.getId();

        // When
        userRepository.deleteById(userId);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    void testUpdateUser() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser);
        
        // When
        savedUser.setEmail("updated@example.com");
        savedUser.setStatus(StatusEnum.INACTIVE);
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(StatusEnum.INACTIVE, updatedUser.getStatus());
        
        // Verify the change is persisted
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> reloadedUser = userRepository.findById(savedUser.getId());
        assertTrue(reloadedUser.isPresent());
        assertEquals("updated@example.com", reloadedUser.get().getEmail());
        assertEquals(StatusEnum.INACTIVE, reloadedUser.get().getStatus());
    }

    @Test
    void testFindAll() {
        // Given
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setStatus(StatusEnum.ACTIVE);
        user1.setRole(RoleEnum.USER);
        user1.setCreatedAt(Instant.now());

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setStatus(StatusEnum.ACTIVE);
        user2.setRole(RoleEnum.ADMIN);
        user2.setCreatedAt(Instant.now());

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        var allUsers = userRepository.findAll();

        // Then
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    void testExistsByEmail() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When & Then
        assertTrue(userRepository.existsById(testUser.getId()));
        
        // Test with findByEmail since existsByEmail is not defined
        assertTrue(userRepository.findByEmail("test@example.com").isPresent());
        assertFalse(userRepository.findByEmail("nonexistent@example.com").isPresent());
    }
}

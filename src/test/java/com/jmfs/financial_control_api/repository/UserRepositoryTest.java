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
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setStatus(StatusEnum.ACTIVE);
        testUser.setRole(RoleEnum.USER);
    }

    @Test
    void testSaveUser() {
        // When
        User savedUser = userRepository.save(testUser);

        // Then
        assertNotNull(savedUser.getId());
        assertEquals(testUser.getName(), savedUser.getName());
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
        assertEquals(testUser.getName(), foundUser.get().getName());
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
    void testFindByUsername_UserExists() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByName("testuser");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getName(), foundUser.get().getName());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        assertEquals(testUser.getPassword(), foundUser.get().getPassword());
        assertEquals(testUser.getStatus(), foundUser.get().getStatus());
        assertEquals(testUser.getRole(), foundUser.get().getRole());
    }

    @Test
    void testFindByUsername_UserNotExists() {
        // When
        Optional<User> foundUser = userRepository.findByName("nonexistentuser");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void testFindByUsername_CaseInsensitive() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> foundUser = userRepository.findByName("TESTUSER");

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
        assertEquals(testUser.getName(), foundUser.get().getName());
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
        savedUser.setName("updateduser");
        savedUser.setEmail("updated@example.com");
        savedUser.setStatus(StatusEnum.INACTIVE);
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertEquals("updateduser", updatedUser.getName());
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals(StatusEnum.INACTIVE, updatedUser.getStatus());
        
        // Verify the change is persisted
        entityManager.flush();
        entityManager.clear();
        
        Optional<User> reloadedUser = userRepository.findById(savedUser.getId());
        assertTrue(reloadedUser.isPresent());
        assertEquals("updateduser", reloadedUser.get().getName());
        assertEquals("updated@example.com", reloadedUser.get().getEmail());
        assertEquals(StatusEnum.INACTIVE, reloadedUser.get().getStatus());
    }

    @Test
    void testFindAll() {
        // Given
        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@example.com");
        user1.setPassword("password1");
        user1.setStatus(StatusEnum.ACTIVE);
        user1.setRole(RoleEnum.USER);

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@example.com");
        user2.setPassword("password2");
        user2.setStatus(StatusEnum.ACTIVE);
        user2.setRole(RoleEnum.ADMIN);

        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        var allUsers = userRepository.findAll();

        // Then
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("user1")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("user2")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user1@example.com")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getEmail().equals("user2@example.com")));
    }

    @Test
    void testExistsBy_EmailAndUsername() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When & Then
        assertTrue(userRepository.existsById(testUser.getId()));
        
        // Test with findByEmail since existsByEmail is not defined
        assertTrue(userRepository.findByEmail("test@example.com").isPresent());
        assertFalse(userRepository.findByEmail("nonexistent@example.com").isPresent());
        
        // Test with findByName since existsByName is not defined
        assertTrue(userRepository.findByName("testuser").isPresent());
        assertFalse(userRepository.findByName("nonexistentuser").isPresent());
    }

    @Test
    void testFindUser_ByEmailAndUsernameReturnSameUser() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> userByEmail = userRepository.findByEmail("test@example.com");
        Optional<User> userByUsername = userRepository.findByName("testuser");

        // Then
        assertTrue(userByEmail.isPresent());
        assertTrue(userByUsername.isPresent());
        assertEquals(userByEmail.get().getId(), userByUsername.get().getId());
        assertEquals(userByEmail.get().getName(), userByUsername.get().getName());
        assertEquals(userByEmail.get().getEmail(), userByUsername.get().getEmail());
    }

    @Test
    void testUniqueConstraints_Username() {
        // Given - Create first user
        User firstUser = new User();
        firstUser.setName("uniqueuser");
        firstUser.setEmail("first@example.com");
        firstUser.setPassword("password123");
        firstUser.setStatus(StatusEnum.ACTIVE);
        firstUser.setRole(RoleEnum.USER);
        entityManager.persistAndFlush(firstUser);

        // When - Try to create second user with same username but different email
        User secondUser = new User();
        secondUser.setName("uniqueuser"); // Same username
        secondUser.setEmail("second@example.com"); // Different email
        secondUser.setPassword("password456");
        secondUser.setStatus(StatusEnum.ACTIVE);
        secondUser.setRole(RoleEnum.USER);

        // Then - Should be able to save (no unique constraint on username in current schema)
        // Note: This test demonstrates current behavior. If you want unique usernames,
        // you'd need to add a unique constraint to the database schema
        User savedSecondUser = userRepository.save(secondUser);
        assertNotNull(savedSecondUser.getId());
        
        // Verify both users exist with same username
        assertEquals(2, userRepository.findAll().size());
    }
}

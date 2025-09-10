package com.jmfs.financial_control_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jmfs.financial_control_api.dto.UserDTO;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.impl.UserServiceImpl;

import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("João Silva");
        user1.setEmail("joao@example.com");
        user1.setPassword("password123");
        user1.setRole(RoleEnum.USER);
        user1.setStatus(StatusEnum.ACTIVE);

        user2 = new User();
        user2.setId(2L);
        user2.setName("Maria Santos");
        user2.setEmail("maria@example.com");
        user2.setPassword("password456");
        user2.setRole(RoleEnum.ADMIN);
        user2.setStatus(StatusEnum.ACTIVE);

        user3 = new User();
        user3.setId(3L);
        user3.setName("Pedro Costa");
        user3.setEmail("pedro@example.com");
        user3.setPassword("password789");
        user3.setRole(RoleEnum.USER);
        user3.setStatus(StatusEnum.INACTIVE);
    }

    @Test
    void testGetUser_WithNameAndRole() {
        // Given
        String name = "João Silva";
        String role = "ROLE_USER";
        List<User> expectedUsers = Arrays.asList(user1);
        
        when(userRepository.findUserByCriteria(name, role)).thenReturn(expectedUsers);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("joao@example.com", result.get(0).email());
        assertEquals("João Silva", result.get(0).name());
        assertEquals("ROLE_USER", result.get(0).role());
        
        verify(userRepository).findUserByCriteria(name, role);
        verify(userRepository, never()).findAll();
    }

    @Test
    void testGetUser_WithNameOnly() {
        // Given
        String name = "Maria Santos";
        String role = null;
        List<User> expectedUsers = Arrays.asList(user2);
        
        when(userRepository.findUserByCriteria(name, role)).thenReturn(expectedUsers);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("maria@example.com", result.get(0).email());
        assertEquals("Maria Santos", result.get(0).name());
        assertEquals("ROLE_ADMIN", result.get(0).role());
        
        verify(userRepository).findUserByCriteria(name, role);
        verify(userRepository, never()).findAll();
    }

    @Test
    void testGetUser_WithRoleOnly() {
        // Given
        String name = null;
        String role = "ROLE_USER";
        List<User> expectedUsers = Arrays.asList(user1, user3);
        
        when(userRepository.findUserByCriteria(name, role)).thenReturn(expectedUsers);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("joao@example.com", result.get(0).email());
        assertEquals("pedro@example.com", result.get(1).email());
        
        verify(userRepository).findUserByCriteria(name, role);
        verify(userRepository, never()).findAll();
    }

    @Test
    void testGetUser_WithoutCriteria_BothNull() {
        // Given
        String name = null;
        String role = null;
        List<User> allUsers = Arrays.asList(user1, user2, user3);
        
        when(userRepository.findAll()).thenReturn(allUsers);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("joao@example.com", result.get(0).email());
        assertEquals("maria@example.com", result.get(1).email());
        assertEquals("pedro@example.com", result.get(2).email());
        
        verify(userRepository).findAll();
        verify(userRepository, never()).findUserByCriteria(anyString(), anyString());
    }

    @Test
    void testGetUser_EmptyResultFromCriteria() {
        // Given
        String name = "Inexistente";
        String role = "ROLE_USER";
        List<User> emptyList = Arrays.asList();
        
        when(userRepository.findUserByCriteria(name, role)).thenReturn(emptyList);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(userRepository).findUserByCriteria(name, role);
        verify(userRepository, never()).findAll();
    }

    @Test
    void testGetUser_EmptyResultFromFindAll() {
        // Given
        String name = null;
        String role = null;
        List<User> emptyList = Arrays.asList();
        
        when(userRepository.findAll()).thenReturn(emptyList);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(userRepository).findAll();
        verify(userRepository, never()).findUserByCriteria(anyString(), anyString());
    }

    @Test
    void testGetUser_VerifyDTOMapping() {
        // Given
        String name = null;
        String role = null;
        List<User> users = Arrays.asList(user1);
        
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<UserDTO> result = userService.getUser(name, role);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        UserDTO dto = result.get(0);
        assertEquals(user1.getEmail(), dto.email());
        assertEquals(user1.getName(), dto.name());
        assertEquals(user1.getRole().getValue(), dto.role());
    }
}

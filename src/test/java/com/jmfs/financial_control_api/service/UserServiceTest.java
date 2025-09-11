package com.jmfs.financial_control_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
    private Pageable pageable;

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

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testGetUser_WithPageable_ReturnsPage() {
        // Given
        List<User> users = Arrays.asList(user1, user2, user3);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDTO> result = userService.getUser(pageable);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        
        // Verify DTO mapping
        UserDTO dto1 = result.getContent().get(0);
        assertEquals("joao@example.com", dto1.email());
        assertEquals("João Silva", dto1.name());
        assertEquals("ROLE_USER", dto1.role());
        
        UserDTO dto2 = result.getContent().get(1);
        assertEquals("maria@example.com", dto2.email());
        assertEquals("Maria Santos", dto2.name());
        assertEquals("ROLE_ADMIN", dto2.role());
        
        UserDTO dto3 = result.getContent().get(2);
        assertEquals("pedro@example.com", dto3.email());
        assertEquals("Pedro Costa", dto3.name());
        assertEquals("ROLE_USER", dto3.role());
        
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetUser_WithPageable_EmptyResult() {
        // Given
        List<User> emptyList = Arrays.asList();
        Page<User> emptyPage = new PageImpl<>(emptyList, pageable, 0);
        
        when(userRepository.findAll(pageable)).thenReturn(emptyPage);

        // When
        Page<UserDTO> result = userService.getUser(pageable);

        // Then
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetUser_WithDifferentPageSize() {
        // Given
        Pageable customPageable = PageRequest.of(1, 5);
        List<User> users = Arrays.asList(user1, user2);
        Page<User> userPage = new PageImpl<>(users, customPageable, 7); // Total de 7 elementos
        
        when(userRepository.findAll(customPageable)).thenReturn(userPage);

        // When
        Page<UserDTO> result = userService.getUser(customPageable);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(7, result.getTotalElements());
        assertEquals(1, result.getNumber()); // Segunda página
        assertEquals(5, result.getSize());
        
        verify(userRepository).findAll(customPageable);
    }

    @Test
    void testGetUser_VerifyDTOMappingWithAllFields() {
        // Given
        List<User> users = Arrays.asList(user1);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        Page<UserDTO> result = userService.getUser(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        
        UserDTO dto = result.getContent().get(0);
        assertEquals(user1.getEmail(), dto.email());
        assertEquals(user1.getName(), dto.name());
        assertEquals(user1.getRole().getValue(), dto.role());
        // Note: password should not be in DTO for security reasons
        
        verify(userRepository).findAll(pageable);
    }

    @Test
    void testGetUser_WithMultiplePages() {
        // Given - Simulando primeira página com 2 itens
        Pageable firstPage = PageRequest.of(0, 2);
        List<User> firstPageUsers = Arrays.asList(user1, user2);
        Page<User> firstUserPage = new PageImpl<>(firstPageUsers, firstPage, 3); // Total de 3 elementos
        
        when(userRepository.findAll(firstPage)).thenReturn(firstUserPage);

        // When
        Page<UserDTO> result = userService.getUser(firstPage);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());
        assertTrue(result.hasNext()); // Tem próxima página
        assertFalse(result.hasPrevious()); // Não tem página anterior
        
        verify(userRepository).findAll(firstPage);
    }
}

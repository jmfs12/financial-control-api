package com.jmfs.financial_control_api.service;

import com.jmfs.financial_control_api.dto.TokenClaimsDTO;
import com.jmfs.financial_control_api.exceptions.AccessDeniedException;
import com.jmfs.financial_control_api.service.spec.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user1;
    private User user2;
    private User user3;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@example.com")
                .password("password123")
                .role(RoleEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Maria Santos")
                .email("maria@example.com")
                .password("password456")
                .role(RoleEnum.ADMIN)
                .status(StatusEnum.ACTIVE)
                .build();

        user3 = User.builder()
                .id(3L)
                .name("Pedro Costa")
                .email("pedro@example.com")
                .password("password789")
                .role(RoleEnum.USER)
                .status(StatusEnum.INACTIVE)
                .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should update all user info succesfully when requester is ADMIN")
    void testPatchUser_case1(){
        String token = "token";
        UserDTO userDTO = new UserDTO(user1.getEmail(), "João Souza", RoleEnum.ADMIN.getValue(), StatusEnum.INACTIVE.getValue());
        TokenClaimsDTO tokenClaimsDTO = new TokenClaimsDTO(user1.getId(), user1.getRole().getValue());


        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(tokenService.extractClaim(token)).thenReturn(tokenClaimsDTO);
        when(userRepository.isAdmin(user1.getId(), RoleEnum.ADMIN)).thenReturn(true);

        userService.patchUser("token", userDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User updatedUser =  captor.getValue();

        assertEquals(userDTO.name(), updatedUser.getName());
        assertEquals(RoleEnum.ADMIN, updatedUser.getRole());
        assertEquals(StatusEnum.INACTIVE, updatedUser.getStatus());
        assertEquals(user1.getEmail(), updatedUser.getEmail());
    }

    @Test
    @DisplayName("Should update user if he is the requester and isn't admin")
    void testPatchUser_case2(){
        String token = "token";
        UserDTO userDTO = new UserDTO(user1.getEmail(), "João Souza", null, null);
        TokenClaimsDTO tokenClaimsDTO = new TokenClaimsDTO(user1.getId(), user1.getRole().getValue());

        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(tokenService.extractClaim(token)).thenReturn(tokenClaimsDTO);
        when(userRepository.isAdmin(user1.getId(), RoleEnum.ADMIN)).thenReturn(false);

        userService.patchUser("token", userDTO);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User updatedUser =  captor.getValue();

        assertEquals(userDTO.name(), updatedUser.getName());
        assertEquals(user1.getRole(), updatedUser.getRole());
        assertEquals(user1.getStatus(), updatedUser.getStatus());
        assertEquals(user1.getEmail(), updatedUser.getEmail());
    }

    @Test
    @DisplayName("Shouldn't update user if he isn't the requester and isn't ADMIN")
    void testPatchUser_case3(){
        String token = "token";
        UserDTO userDTO = new UserDTO(user1.getEmail(), "João Souza", null, null);
        TokenClaimsDTO tokenClaimsDTO = new TokenClaimsDTO(user3.getId(), user3.getRole().getValue());

        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(tokenService.extractClaim(token)).thenReturn(tokenClaimsDTO);
        when(userRepository.isAdmin(user3.getId(), RoleEnum.ADMIN)).thenReturn(false);


        assertThrows(
                AccessDeniedException.class,
                () -> userService.patchUser("token", userDTO)
        );
    }

    @Test
    @DisplayName("Shouldn't update user when he isn't ADMIN, is the requester, but try to update the role or status")
    void testPatchUser_case4(){
        String token = "token";
        UserDTO userDTO = new UserDTO(user1.getEmail(), "João Souza", RoleEnum.ADMIN.getValue(), null);
        TokenClaimsDTO tokenClaimsDTO = new TokenClaimsDTO(user3.getId(), user3.getRole().getValue());

        when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
        when(tokenService.extractClaim(token)).thenReturn(tokenClaimsDTO);
        when(userRepository.isAdmin(user3.getId(), RoleEnum.ADMIN)).thenReturn(false);


        assertThrows(
                AccessDeniedException.class,
                () -> userService.patchUser("token", userDTO)
        );
    }

    @Test
    void testGetUser_WithPageable_ReturnsPage() {
        List<User> users = Arrays.asList(user1, user2, user3);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());
        
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> result = userService.getUser(pageable);

        assertNotNull(result);
        assertEquals(3, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());
        
        UserDTO dto1 = result.getContent().getFirst();
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
        List<User> emptyList = List.of();
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
    void testGetUser_WithMultiplePages() {
        Pageable firstPage = PageRequest.of(0, 2);
        List<User> firstPageUsers = Arrays.asList(user1, user2);
        Page<User> firstUserPage = new PageImpl<>(firstPageUsers, firstPage, 3); // Total de 3 elementos
        
        when(userRepository.findAll(firstPage)).thenReturn(firstUserPage);

        Page<UserDTO> result = userService.getUser(firstPage);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getNumber());
        assertEquals(2, result.getSize());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
        
        verify(userRepository).findAll(firstPage);
    }


}

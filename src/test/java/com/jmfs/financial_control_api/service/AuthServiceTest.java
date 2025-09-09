package com.jmfs.financial_control_api.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.impl.AuthServiceImpl;
import com.jmfs.financial_control_api.service.spec.TokenService;
import com.jmfs.financial_control_api.dto.AuthRequest;
import com.jmfs.financial_control_api.dto.AuthResponse;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.exceptions.UserAlreadyExistsException;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.exceptions.WrongPasswordException;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void testLoginSuccess() {
        String email = "test@example.com";
        String password = "123456";
        String encodedPassword = "encoded123456";
        String token = "token";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn(token);

        AuthRequest request = new AuthRequest(email, password);
        AuthResponse response = authService.login(request);

        assertEquals(email, response.email());
        assertEquals(token, response.token());
    }

    @Test
    void testLoginUserNotFound() {
        String email = "notfound@example.com";
        AuthRequest request = new AuthRequest(email, "pass");
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(
            UserNotFoundException.class,
            () -> authService.login(request)
        );
    }

    @Test
    void testLoginWrongPassword() {
        String email = "test@example.com";
        String password = "wrongpass";
        String encodedPassword = "encoded123456";
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);
        AuthRequest request = new AuthRequest(email, password);
        assertThrows(
            WrongPasswordException.class,
            () -> authService.login(request)
        );
    }

    @Test
    void testRegisterSuccess() {
        String email = "newuser@example.com";
        String password = "123456";
        String encodedPassword = "encoded123456";
        String token = "token";

        AuthRequest request = new AuthRequest(email, password);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(tokenService.generateToken(any())).thenReturn(token);
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthResponse response = authService.register(request);
        
        assertEquals(email, response.email());
        assertEquals(token, response.token());
    }

    @Test
    void testRegisterUserAlreadyExists() {
        String email = "existing@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        AuthRequest request = new AuthRequest(email, "pass");
        assertThrows(
            UserAlreadyExistsException.class,
            () -> authService.register(request)
        );
    }
}

package com.jmfs.financial_control_api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.repository.IUserRepository;
import com.jmfs.financial_control_api.service.impl.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsTest {
    @Mock
    private IUserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("Should return user succesfully when everything is ok")
    void loadUserByUsernameCase1(){
        User user = new User();
        user.setEmail("email@gmail.com");
        user.setPassword("123");
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("email@gmail.com");

        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());

        verify(userRepository, times(1)).findByEmail("email@gmail.com");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void loadUserByUsernameCase2(){
        when(userRepository.findByEmail("email@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("email@gmail.com");
        });
        verify(userRepository, times(1)).findByEmail("email@gmail.com");
    }
}

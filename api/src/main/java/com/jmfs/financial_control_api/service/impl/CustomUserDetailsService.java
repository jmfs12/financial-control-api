package com.jmfs.financial_control_api.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jmfs.financial_control_api.config.security.CustomUserDetails;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.repository.IUserRepository;
import com.jmfs.financial_control_api.service.spec.ICustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements ICustomUserDetailsService{
    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        log.debug("[USER DETAILS SERVICE] Finding user with email: " + email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return new CustomUserDetails(user);
    }
}

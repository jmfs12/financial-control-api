package com.jmfs.financial_control_api.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.jmfs.financial_control_api.config.security.CustomUserDetails;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.CustomUserDetailsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email){
        log.debug("[USER DETAILS SERVICE] Finding user with email: " + email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return new CustomUserDetails(user);
    }
}

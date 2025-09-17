package com.jmfs.financial_control_api.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jmfs.financial_control_api.dto.AuthRequest;
import com.jmfs.financial_control_api.dto.AuthResponse;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import com.jmfs.financial_control_api.exceptions.UserAlreadyExistsException;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.exceptions.WrongPasswordException;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.AuthService;
import com.jmfs.financial_control_api.service.spec.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest authRequest){
        log.debug("[AUTH SERVICE] Attempting to log in user: {}", authRequest.email());
        User user = userRepository.findByEmail(authRequest.email())
                    .orElseThrow(() -> new UserNotFoundException("User not found: " + authRequest.email() ));
        if(passwordEncoder.matches(authRequest.password(), user.getPassword())){
            String token = tokenService.generateToken(user);
            log.debug("[AUTH SERVICE] User logged in succesfully, {}", authRequest.email());
            return new AuthResponse(user.getEmail(), token);
        }
        else throw new WrongPasswordException("Incorrect password for user: " + authRequest.email());
    }
    public AuthResponse register(AuthRequest authRequest){
        log.debug("[AUTH SERVICE] Attempting to register user: {}", authRequest.email());
        if(userRepository.findByEmail(authRequest.email()).isPresent())
            throw new UserAlreadyExistsException("User already exists: " + authRequest.email());
        
        User user = User.builder()
                .name(authRequest.username())
                .email(authRequest.email())
                .password(passwordEncoder.encode(authRequest.password()))
                .status(StatusEnum.ACTIVE)
                .role(RoleEnum.USER)
                .build();

        userRepository.save(user);

        String token = tokenService.generateToken(user);
        log.debug("[AUTH SERVICE] User created succesfully: {}", authRequest.email());
        return new AuthResponse(user.getEmail(), token);
    }
}

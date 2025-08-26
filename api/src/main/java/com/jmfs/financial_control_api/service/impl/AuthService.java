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
import com.jmfs.financial_control_api.repository.IUserRepository;
import com.jmfs.financial_control_api.service.spec.IAuthService;
import com.jmfs.financial_control_api.service.spec.ITokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService implements IAuthService{
    private final IUserRepository userRepository;
    private final ITokenService tokenService;
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
        
        User user = new User();
        user.setEmail(authRequest.email());
        user.setPassword(passwordEncoder.encode(authRequest.password()));
        user.setStatus(StatusEnum.ACTIVE);
        user.setRole(RoleEnum.USER);
        userRepository.save(user);

        String token = tokenService.generateToken(user);
        log.debug("[AUTH SERVICE] User created succesfully: {}", authRequest.email());
        return new AuthResponse(user.getEmail(), token);
    }
}

package com.jmfs.financial_control_api.service.impl;

import org.springframework.stereotype.Service;

import com.jmfs.financial_control_api.dto.UserDTO;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    
    private final UserRepository userRepository;

    @Override
    public UserDTO getUser(Long id){
        return null;
    }
}

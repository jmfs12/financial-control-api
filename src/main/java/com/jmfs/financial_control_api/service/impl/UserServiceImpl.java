package com.jmfs.financial_control_api.service.impl;

import java.util.List;

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
    public List<UserDTO> getUser(String name, String role){
        if(name != null || role != null) {
            return userRepository.findUserByCriteria(name, role)
                                    .stream()
                                    .map(UserDTO::fromEntity)
                                    .toList();
        }
        return userRepository.findAll()
                                .stream()
                                .map(UserDTO::fromEntity)
                                .toList();
    }
}

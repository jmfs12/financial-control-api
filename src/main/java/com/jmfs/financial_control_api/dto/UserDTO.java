package com.jmfs.financial_control_api.dto;

import com.jmfs.financial_control_api.entity.User;

public record UserDTO(String email, String name, String role, String status) {
    public static UserDTO fromEntity(User user){
        return new UserDTO(user.getEmail(), user.getName(), user.getRole().getValue(), user.getStatus().getValue());
    }
}

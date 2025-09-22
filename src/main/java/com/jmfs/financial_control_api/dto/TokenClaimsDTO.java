package com.jmfs.financial_control_api.dto;

import com.jmfs.financial_control_api.entity.User;

public record TokenClaimsDTO(Long id, String role) {

    public static TokenClaimsDTO fromEntity(User user){
        return new TokenClaimsDTO(user.getId(), user.getRole().getValue());
    }

}

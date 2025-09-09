package com.jmfs.financial_control_api.service.spec;

import com.jmfs.financial_control_api.entity.User;

public interface TokenService {
    public String generateToken(User user);
    public String validateToken(String token);
    public Long extractUserId(String token);
}

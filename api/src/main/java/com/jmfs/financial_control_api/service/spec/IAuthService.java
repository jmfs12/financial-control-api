package com.jmfs.financial_control_api.service.spec;

import com.jmfs.financial_control_api.dto.AuthRequest;
import com.jmfs.financial_control_api.dto.AuthResponse;

public interface IAuthService {
    public AuthResponse login(AuthRequest authRequest);
    public AuthResponse register(AuthRequest authRequest);
}

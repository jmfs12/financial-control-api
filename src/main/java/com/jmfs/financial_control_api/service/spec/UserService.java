package com.jmfs.financial_control_api.service.spec;

import java.util.List;

import com.jmfs.financial_control_api.dto.UserDTO;

public interface UserService {
    public List<UserDTO> getUser(String name, String role);
    
}

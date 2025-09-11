package com.jmfs.financial_control_api.service.spec;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jmfs.financial_control_api.dto.UserDTO;

public interface UserService {
    public Page<UserDTO> getUser(Pageable pageable);
    
}

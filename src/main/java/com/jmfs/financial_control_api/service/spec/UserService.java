package com.jmfs.financial_control_api.service.spec;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jmfs.financial_control_api.dto.UserDTO;

import java.util.List;

public interface UserService {
    public Page<UserDTO> getUser(Pageable pageable);

    public void deleteUsers(List<Long> ids);

    public void deleteUserById(Long id);

    public void patchUser(String token, UserDTO userDTO);
}

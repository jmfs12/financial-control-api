package com.jmfs.financial_control_api.service.impl;

import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import com.jmfs.financial_control_api.exceptions.AccessDeniedException;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.service.spec.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jmfs.financial_control_api.dto.UserDTO;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService{
    
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public Page<UserDTO> getUser(Pageable pageable){
        log.debug("[USER SERVICE] Getting Users for page {}", pageable.getPageNumber());
        return userRepository.findAll(pageable).map(UserDTO::fromEntity);
    }
    @Override
    public void deleteUsers(List<Long> ids){
        log.debug("[USER SERVICE] Deleting Users for {}", ids);
        if (ids == null || ids.isEmpty()){
            log.warn("[USER SERVICE] IDS are empty");
            return;
        } userRepository.deleteByIds(ids);
    }

    @Override
    public void deleteUserById(Long id) {
        log.debug("[USER SERVICE] Deleting User {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public void patchUser(String token, UserDTO userDTO) {
        log.debug("[USER SERVICE] Patching User {}", userDTO);
        User userToUpdate = userRepository.findByEmail(userDTO.email())
                .orElseThrow(() -> new UserNotFoundException(userDTO.email()));

        Long requesterId = tokenService.extractClaim(token).id();
        boolean isRequesterAdmin = userRepository.isAdmin(requesterId, RoleEnum.ADMIN);

        if (isRequesterAdmin) {
            updateRoleIfProvided(userToUpdate, userDTO);
            updateStatusIfProvided(userToUpdate, userDTO);
        } else {
            log.debug("[USER SERVICE] User {} has no role admin", userDTO.email());
            if (!userToUpdate.getId().equals(requesterId)) {
                throw new AccessDeniedException("User don't have permission to update user");
            }
            if (userDTO.role() != null || userDTO.status() != null) {
                throw new AccessDeniedException("User don't have permission to update role or status");
            }
        }

        if (userDTO.name() != null) {
            userToUpdate.setName(userDTO.name());
        }

        userRepository.save(userToUpdate);
    }

    private void updateRoleIfProvided(User user, UserDTO dto) {
        if (dto.role() != null) {
            RoleEnum newRole = RoleEnum.fromString(dto.role());
            user.setRole(newRole);
        }
    }

    private void updateStatusIfProvided(User user, UserDTO dto) {
        if (dto.status() != null) {
            StatusEnum newStatus = StatusEnum.fromString(dto.status());
            user.setStatus(newStatus);
        }
    }



}

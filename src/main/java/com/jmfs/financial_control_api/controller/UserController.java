package com.jmfs.financial_control_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.jmfs.financial_control_api.dto.UserDTO;
import com.jmfs.financial_control_api.service.spec.UserService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable){
        return ResponseEntity.ok(userService.getUser(pageable));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping
    public ResponseEntity<?>  deleteUser(@RequestParam Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/all")
    public ResponseEntity<?> deleteAllUsersByIds(@RequestParam List<Long> ids) {
        userService.deleteUsers(ids);
        return ResponseEntity.ok().build();
    }

    @PatchMapping()
    public ResponseEntity<?> updateUser(@RequestHeader String token, @RequestBody UserDTO userDTO) {
        userService.patchUser(token.substring(7), userDTO);
        return ResponseEntity.ok().build();
    }

}

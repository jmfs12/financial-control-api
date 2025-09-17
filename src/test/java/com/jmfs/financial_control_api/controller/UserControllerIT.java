package com.jmfs.financial_control_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmfs.financial_control_api.dto.UserDTO;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import com.jmfs.financial_control_api.repository.UserRepository;
import com.jmfs.financial_control_api.service.spec.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    private User testUser;
    private User testAdmin;

    @BeforeEach
    void setup(){
        testUser = User.builder()
                .name("user")
                .email("user@gmail.com")
                .password("userpassword")
                .role(RoleEnum.USER)
                .status(StatusEnum.ACTIVE)
                .build();

        testAdmin = User.builder()
                .name("admin")
                .email("admin@gmail.com")
                .password("adminpassword")
                .role(RoleEnum.ADMIN)
                .status(StatusEnum.ACTIVE)
                .build();

        userRepository.save(testUser);
        userRepository.save(testAdmin);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_AsAdmin_ShouldReturnUserPage() throws Exception {
        mockMvc.perform(get("/api/users")
                    .param("page","0")
                    .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.content[1].email").value(testAdmin.getEmail()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_AsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users")
                        .param("id", String.valueOf(testUser.getId())))
                .andExpect(status().isOk());

        assertTrue(userRepository.findById(testUser.getId()).isEmpty(), "User 1 must be deleted");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUsersList_AsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/all")
                        .param("ids", String.valueOf(testUser.getId()), String.valueOf(testAdmin.getId())))
                .andExpect(status().isOk());

        assertTrue(userRepository.findById(testUser.getId()).isEmpty(), "User 1 must be deleted");
        assertTrue(userRepository.findById(testAdmin.getId()).isEmpty(), "User 2 must be deleted");
    }

//    @Test
//    void updateUser_AsAdmin() throws Exception {
//        UserDTO updateRequest = new UserDTO(testUser.getEmail(), "other name", RoleEnum.ADMIN.getValue(), null);
//
//        mockMvc.perform(patch("/api/users")
//                        .header("token", token)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateRequest)))
//                .andExpect(status().isOk());
//
//        User updatedUser = userRepository.findByEmail(testUser.getEmail()).get();
//        assertEquals("other name", updatedUser.getName());
//        assertEquals(RoleEnum.ADMIN, updatedUser.getRole());
//    }
//
//    void updateUser_AsUser_ShouldReturnForbiddenWhenUpdatingAnotherUser() throws Exception {
//
//    }

}

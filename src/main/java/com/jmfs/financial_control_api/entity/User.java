package com.jmfs.financial_control_api.entity;

import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name="users")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "username")
    private String name;

    @Column(name="email")
    private String email;

    @Column(name="password_hash")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private StatusEnum status;
    
    @Enumerated(EnumType.STRING)
    @Column(name="role")
    private RoleEnum role;

    @Column(name="created_at")
    private final Instant createdAt = Instant.now();


}

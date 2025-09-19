package com.jmfs.financial_control_api.entity;

import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import com.jmfs.financial_control_api.entity.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
@Builder
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

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    ) private List<Account> accounts = new ArrayList<>();

}

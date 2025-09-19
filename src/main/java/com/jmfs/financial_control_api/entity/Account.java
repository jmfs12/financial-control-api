package com.jmfs.financial_control_api.entity;

import com.jmfs.financial_control_api.entity.enums.TypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @Column(name="name")
    private String name;

    @Column(name="type")
    @Enumerated(EnumType.STRING)
    private TypeEnum type;

    @Column(name="currency")
    private String currency;

    @Column(name="balance_snapshot")
    private BigDecimal balance_snapshot;

    @Column(name="institution")
    private String institution;

    @Column(name="created_at")
    private final Instant created_at =  Instant.now();
}

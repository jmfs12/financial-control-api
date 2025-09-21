package com.jmfs.financial_control_api.repository;

import com.jmfs.financial_control_api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByName(String name);

    boolean existsByNameAndUserId(String name, Long userId);
}

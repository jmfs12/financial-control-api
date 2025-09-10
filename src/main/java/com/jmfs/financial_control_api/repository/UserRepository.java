package com.jmfs.financial_control_api.repository;

import com.jmfs.financial_control_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String username);

    @Query("SELECT u FROM User u WHERE (:name IS NULL OR u.name = :name) AND (:role IS NULL OR u.role = :role)")
    List<User> findUserByCriteria(@Param("name") String name, @Param("role") String role);
}

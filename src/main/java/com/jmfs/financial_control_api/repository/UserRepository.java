package com.jmfs.financial_control_api.repository;

import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.entity.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM User u WHERE u.id IN :ids")
    void deleteByIds(@Param("ids") List<Long> ids);

    @Query("SELECT COUNT(u) > 0  FROM User u JOIN u.role r WHERE u.id = :userId AND r = :userRole")
    Boolean isAdmin(@Param("userId") Long userId, @Param("userRole") RoleEnum userRole);
}

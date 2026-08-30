package com.skillbridge.user.repository;

import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE (:role IS NULL OR u.role = :role) " +
           "AND (:isActive IS NULL OR u.active = :isActive) " +
           "AND (CAST(:search AS string) IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<User> findUsers(
            @Param("role") Role role,
            @Param("isActive") Boolean isActive,
            @Param("search") String search,
            Pageable pageable
    );
}
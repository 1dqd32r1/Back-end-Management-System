package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    List<User> findByStatus(String status);

    List<User> findByUsernameContainingIgnoreCase(String username);

    long countByStatus(String status);

    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR u.username LIKE %:keyword% OR " +
           "u.email LIKE %:keyword% OR u.phone LIKE %:keyword% OR " +
           "u.nickname LIKE %:keyword%) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "u.delFlag = '0'")
    Page<User> searchUsers(@Param("keyword") String keyword,
                          @Param("status") String status,
                          Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.delFlag = '0'")
    List<User> findAllActiveUsers();

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.delFlag = '0'")
    Optional<User> findActiveByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.delFlag = '0'")
    List<User> findActiveByRole(@Param("role") String role);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.delFlag = '0'")
    List<User> findActiveByStatus(@Param("status") String status);

    long countByDelFlag(String delFlag);
}

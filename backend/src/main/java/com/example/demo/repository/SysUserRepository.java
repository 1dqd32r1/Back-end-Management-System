package com.example.demo.repository;

import com.example.demo.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUserName(String userName);

    @Query("SELECT r.roleName FROM SysUser u JOIN u.roles r WHERE u.userId = :userId")
    List<String> findRoleNamesByUserId(@Param("userId") Long userId);

    @Query("SELECT p.postId FROM SysUser u JOIN u.posts p WHERE u.userId = :userId")
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);
}
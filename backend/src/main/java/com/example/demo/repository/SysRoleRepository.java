package com.example.demo.repository;

import com.example.demo.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysRoleRepository extends JpaRepository<SysRole, Long> {

    @Query("SELECT r FROM SysRole r JOIN r.users u WHERE u.userId = :userId")
    List<SysRole> findByUserId(@Param("userId") Long userId);

    @Query("SELECT r FROM SysRole r JOIN r.users u WHERE u.userId = :userId ORDER BY r.roleSort DESC")
    List<SysRole> findByUserIdOrderByRoleSortDesc(@Param("userId") Long userId);
}
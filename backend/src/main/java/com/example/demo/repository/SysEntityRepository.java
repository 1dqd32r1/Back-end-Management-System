package com.example.demo.repository;

import com.example.demo.entity.SysEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SysEntityRepository extends JpaRepository<SysEntity, Long> {

    @Query("SELECT e.entityId FROM SysEntity e JOIN e.users u WHERE u.userId = :userId")
    Set<Long> findAccessibleEntityIds(@Param("userId") Long userId);
}
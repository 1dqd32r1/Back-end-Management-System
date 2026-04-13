package com.example.demo.repository;

import com.example.demo.entity.SysMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysMenuRepository extends JpaRepository<SysMenu, Long> {

    @Query("SELECT m.perms FROM SysMenu m JOIN m.roles r JOIN r.users u WHERE u.userId = :userId")
    List<String> findPermissionCodesByUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM SysMenu m JOIN m.roles r JOIN r.users u WHERE u.userId = :userId AND m.menuType = 'C' ORDER BY m.orderNum")
    List<SysMenu> findUserMenus(@Param("userId") Long userId);
}
package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/system/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取用户权限信息
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('system:permission:view')")
    public ResponseEntity<UserPermission> getUserPermission(@PathVariable Long userId) {
        UserPermission permission = permissionService.getUserPermission(userId);
        return ResponseEntity.ok(permission);
    }

    /**
     * 获取用户可访问的菜单
     */
    @GetMapping("/menu/list")
    @PreAuthorize("hasAuthority('system:menu:view')")
    public ResponseEntity<List<SysMenu>> getUserMenus() {
        // TODO: 实现获取用户菜单逻辑
        return ResponseEntity.ok(Collections.emptyList());
    }

    /**
     * 检查用户权限
     */
    @PostMapping("/check")
    @PreAuthorize("hasAuthority('system:permission:check')")
    public ResponseEntity<Boolean> checkPermission(@RequestParam Long userId, @RequestParam String permission) {
        // TODO: 实现权限检查逻辑
        return ResponseEntity.ok(true);
    }
}
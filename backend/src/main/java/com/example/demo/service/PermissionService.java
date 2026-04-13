package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.SysUserRepository;
import com.example.demo.repository.SysRoleRepository;
import com.example.demo.repository.SysMenuRepository;
import com.example.demo.repository.SysEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    @Autowired
    private SysUserRepository userRepository;

    @Autowired
    private SysRoleRepository roleRepository;

    @Autowired
    private SysMenuRepository menuRepository;

    @Autowired
    private SysEntityRepository entityRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 高性能获取用户权限（<50ms）
    @Cacheable(value = "userPermission", key = "#userId")
    public UserPermission getUserPermission(Long userId) {
        return calculateUserPermission(userId);
    }

    @Transactional(readOnly = true)
    private UserPermission calculateUserPermission(Long userId) {
        // 1. 获取用户基础信息
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 2. 获取角色列表
        List<String> roles = userRepository.findRoleNamesByUserId(userId);

        // 3. 获取权限标识
        List<String> permissions = menuRepository.findPermissionCodesByUserId(userId);

        // 4. 获取实体权限
        Set<Long> accessibleEntities = entityRepository.findAccessibleEntityIds(userId);

        // 5. 获取数据范围
        String dataScope = getUserDataScope(userId);

        // 6. 获取部门ID
        Long departmentId = null;
        if (user.getDept() != null) {
            departmentId = user.getDept().getDeptId();
        }

        // 7. 获取岗位ID
        List<Long> postIds = userRepository.findPostIdsByUserId(userId);

        return UserPermission.builder()
                .userId(userId)
                .roles(roles)
                .permissions(permissions)
                .accessibleEntities(accessibleEntities)
                .dataScope(dataScope)
                .departmentId(departmentId)
                .postIds(postIds)
                .build();
    }

    private String getUserDataScope(Long userId) {
        // 获取用户最高角色
        List<SysRole> roles = roleRepository.findByUserId(userId);
        if (roles.isEmpty()) {
            return "1"; // 默认全部数据权限
        }

        // 获取最高角色（按角色排序）
        SysRole highestRole = roles.stream()
                .filter(r -> r.getRoleSort() != null)
                .max(Comparator.comparingInt(SysRole::getRoleSort))
                .orElse(roles.get(0));

        return highestRole.getDataScope() != null ? highestRole.getDataScope() : "1";
    }
}

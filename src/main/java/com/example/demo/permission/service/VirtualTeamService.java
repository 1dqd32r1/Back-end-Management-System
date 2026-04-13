package com.example.demo.permission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.permission.entity.SysVirtualTeam;
import com.example.demo.permission.entity.SysVirtualTeamMember;

import java.util.List;

/**
 * 虚拟团队服务接口
 */
public interface VirtualTeamService extends IService<SysVirtualTeam> {

    /**
     * 创建虚拟团队
     */
    SysVirtualTeam createTeam(SysVirtualTeam team, List<Long> memberIds);

    /**
     * 添加成员
     */
    void addMembers(Long teamId, List<Long> userIds, String role);

    /**
     * 移除成员
     */
    void removeMembers(Long teamId, List<Long> userIds);

    /**
     * 设置负责人
     */
    void setLeader(Long teamId, Long userId);

    /**
     * 配置权限
     */
    void configurePermissions(Long teamId, List<String> inheritedPermissions, List<String> exclusivePermissions);

    /**
     * 获取用户的虚拟团队
     */
    List<SysVirtualTeam> getUserTeams(Long userId);

    /**
     * 获取团队成员
     */
    List<SysVirtualTeamMember> getTeamMembers(Long teamId);

    /**
     * 解散团队
     */
    void dissolveTeam(Long teamId);
}

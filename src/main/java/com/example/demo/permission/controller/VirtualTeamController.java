package com.example.demo.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.common.Result;
import com.example.demo.permission.entity.SysVirtualTeam;
import com.example.demo.permission.entity.SysVirtualTeamMember;
import com.example.demo.permission.service.VirtualTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 虚拟团队控制器
 */
@RestController
@RequestMapping("/api/permission/virtual-team")
@RequiredArgsConstructor
public class VirtualTeamController {

    private final VirtualTeamService virtualTeamService;

    /**
     * 获取虚拟团队列表
     */
    @GetMapping("/list")
    public Result<List<SysVirtualTeam>> list(
            @RequestParam(required = false) String teamType,
            @RequestParam(required = false) Integer status) {

        QueryWrapper<SysVirtualTeam> wrapper = new QueryWrapper<>();
        if (teamType != null) {
            wrapper.eq("team_type", teamType);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("created_at");

        return Result.success(virtualTeamService.list(wrapper));
    }

    /**
     * 获取团队详情
     */
    @GetMapping("/{teamId}")
    public Result<SysVirtualTeam> get(@PathVariable Long teamId) {
        return Result.success(virtualTeamService.getById(teamId));
    }

    /**
     * 创建虚拟团队
     */
    @PostMapping("/create")
    public Result<SysVirtualTeam> create(@RequestBody Map<String, Object> params) {
        SysVirtualTeam team = new SysVirtualTeam();
        team.setTeamName((String) params.get("teamName"));
        team.setTeamCode((String) params.get("teamCode"));
        team.setTeamType((String) params.get("teamType"));
        team.setProjectId(params.get("projectId") != null ? Long.valueOf(params.get("projectId").toString()) : null);
        team.setParentDeptId(params.get("parentDeptId") != null ? Long.valueOf(params.get("parentDeptId").toString()) : null);

        @SuppressWarnings("unchecked")
        List<Long> memberIds = (List<Long>) params.get("memberIds");

        return Result.success(virtualTeamService.createTeam(team, memberIds));
    }

    /**
     * 更新团队信息
     */
    @PutMapping("/{teamId}")
    public Result<?> update(@PathVariable Long teamId, @RequestBody SysVirtualTeam team) {
        team.setTeamId(teamId);
        virtualTeamService.updateById(team);
        return Result.success();
    }

    /**
     * 解散团队
     */
    @DeleteMapping("/{teamId}")
    public Result<?> dissolve(@PathVariable Long teamId) {
        virtualTeamService.dissolveTeam(teamId);
        return Result.success();
    }

    /**
     * 获取团队成员
     */
    @GetMapping("/{teamId}/members")
    public Result<List<SysVirtualTeamMember>> getMembers(@PathVariable Long teamId) {
        return Result.success(virtualTeamService.getTeamMembers(teamId));
    }

    /**
     * 添加成员
     */
    @PostMapping("/{teamId}/members")
    public Result<?> addMembers(@PathVariable Long teamId, @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) params.get("userIds");
        String role = (String) params.get("role");
        virtualTeamService.addMembers(teamId, userIds, role);
        return Result.success();
    }

    /**
     * 移除成员
     */
    @DeleteMapping("/{teamId}/members")
    public Result<?> removeMembers(@PathVariable Long teamId, @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) params.get("userIds");
        virtualTeamService.removeMembers(teamId, userIds);
        return Result.success();
    }

    /**
     * 设置负责人
     */
    @PostMapping("/{teamId}/leader")
    public Result<?> setLeader(@PathVariable Long teamId, @RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        virtualTeamService.setLeader(teamId, userId);
        return Result.success();
    }

    /**
     * 配置权限
     */
    @PutMapping("/{teamId}/permissions")
    public Result<?> configurePermissions(@PathVariable Long teamId, @RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<String> inheritedPermissions = (List<String>) params.get("inheritedPermissions");
        @SuppressWarnings("unchecked")
        List<String> exclusivePermissions = (List<String>) params.get("exclusivePermissions");
        virtualTeamService.configurePermissions(teamId, inheritedPermissions, exclusivePermissions);
        return Result.success();
    }

    /**
     * 获取用户的虚拟团队
     */
    @GetMapping("/user/{userId}")
    public Result<List<SysVirtualTeam>> getUserTeams(@PathVariable Long userId) {
        return Result.success(virtualTeamService.getUserTeams(userId));
    }
}

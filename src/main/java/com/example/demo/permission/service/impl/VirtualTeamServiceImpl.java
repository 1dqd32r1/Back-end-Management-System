package com.example.demo.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.permission.entity.SysVirtualTeam;
import com.example.demo.permission.entity.SysVirtualTeamMember;
import com.example.demo.permission.mapper.SysVirtualTeamMapper;
import com.example.demo.permission.mapper.SysVirtualTeamMemberMapper;
import com.example.demo.permission.service.VirtualTeamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟团队服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VirtualTeamServiceImpl extends ServiceImpl<SysVirtualTeamMapper, SysVirtualTeam> implements VirtualTeamService {

    private final SysVirtualTeamMemberMapper memberMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public SysVirtualTeam createTeam(SysVirtualTeam team, List<Long> memberIds) {
        team.setMemberCount(memberIds != null ? memberIds.size() : 0);
        team.setStatus(1);
        save(team);

        // 添加成员
        if (memberIds != null && !memberIds.isEmpty()) {
            for (Long userId : memberIds) {
                SysVirtualTeamMember member = new SysVirtualTeamMember();
                member.setTeamId(team.getTeamId());
                member.setUserId(userId);
                member.setMemberRole("MEMBER");
                member.setJoinTime(LocalDateTime.now());
                memberMapper.insert(member);
            }
        }

        log.info("Created virtual team: {}", team.getTeamCode());
        return team;
    }

    @Override
    @Transactional
    public void addMembers(Long teamId, List<Long> userIds, String role) {
        SysVirtualTeam team = getById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }

        for (Long userId : userIds) {
            // 检查是否已是成员
            Long count = memberMapper.selectCount(new QueryWrapper<SysVirtualTeamMember>()
                    .eq("team_id", teamId)
                    .eq("user_id", userId));
            if (count == 0) {
                SysVirtualTeamMember member = new SysVirtualTeamMember();
                member.setTeamId(teamId);
                member.setUserId(userId);
                member.setMemberRole(role != null ? role : "MEMBER");
                member.setJoinTime(LocalDateTime.now());
                memberMapper.insert(member);
            }
        }

        // 更新成员数量
        Long memberCount = memberMapper.selectCount(new QueryWrapper<SysVirtualTeamMember>()
                .eq("team_id", teamId));
        team.setMemberCount(memberCount.intValue());
        updateById(team);

        log.info("Added {} members to team {}", userIds.size(), teamId);
    }

    @Override
    @Transactional
    public void removeMembers(Long teamId, List<Long> userIds) {
        for (Long userId : userIds) {
            memberMapper.delete(new QueryWrapper<SysVirtualTeamMember>()
                    .eq("team_id", teamId)
                    .eq("user_id", userId));
        }

        // 更新成员数量
        SysVirtualTeam team = getById(teamId);
        Long memberCount = memberMapper.selectCount(new QueryWrapper<SysVirtualTeamMember>()
                .eq("team_id", teamId));
        team.setMemberCount(memberCount.intValue());
        updateById(team);

        log.info("Removed {} members from team {}", userIds.size(), teamId);
    }

    @Override
    @Transactional
    public void setLeader(Long teamId, Long userId) {
        SysVirtualTeam team = getById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }

        // 更新团队负责人
        team.setLeaderId(userId);
        updateById(team);

        // 更新成员角色
        SysVirtualTeamMember member = memberMapper.selectOne(new QueryWrapper<SysVirtualTeamMember>()
                .eq("team_id", teamId)
                .eq("user_id", userId));
        if (member != null) {
            member.setMemberRole("LEADER");
            memberMapper.updateById(member);
        }

        log.info("Set leader {} for team {}", userId, teamId);
    }

    @Override
    @Transactional
    public void configurePermissions(Long teamId, List<String> inheritedPermissions, List<String> exclusivePermissions) {
        SysVirtualTeam team = getById(teamId);
        if (team == null) {
            throw new RuntimeException("团队不存在");
        }

        try {
            if (inheritedPermissions != null) {
                team.setInheritedPermissions(objectMapper.writeValueAsString(inheritedPermissions));
            }
            if (exclusivePermissions != null) {
                team.setExclusivePermissions(objectMapper.writeValueAsString(exclusivePermissions));
            }
            updateById(team);
            log.info("Configured permissions for team {}", teamId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("权限配置序列化失败", e);
        }
    }

    @Override
    public List<SysVirtualTeam> getUserTeams(Long userId) {
        // 查询用户所属的团队ID
        List<SysVirtualTeamMember> members = memberMapper.selectList(new QueryWrapper<SysVirtualTeamMember>()
                .eq("user_id", userId));

        if (members.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> teamIds = members.stream().map(SysVirtualTeamMember::getTeamId).toList();
        return list(new QueryWrapper<SysVirtualTeam>()
                .in("team_id", teamIds)
                .eq("status", 1));
    }

    @Override
    public List<SysVirtualTeamMember> getTeamMembers(Long teamId) {
        return memberMapper.selectList(new QueryWrapper<SysVirtualTeamMember>()
                .eq("team_id", teamId));
    }

    @Override
    @Transactional
    public void dissolveTeam(Long teamId) {
        // 删除所有成员
        memberMapper.delete(new QueryWrapper<SysVirtualTeamMember>().eq("team_id", teamId));

        // 更新团队状态
        SysVirtualTeam team = getById(teamId);
        if (team != null) {
            team.setStatus(0);
            updateById(team);
        }

        log.info("Dissolved team {}", teamId);
    }
}

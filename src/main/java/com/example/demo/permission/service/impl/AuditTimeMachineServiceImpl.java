package com.example.demo.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.permission.entity.PermAuditSnapshot;
import com.example.demo.permission.mapper.PermAuditSnapshotMapper;
import com.example.demo.permission.service.AuditTimeMachineService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 权限审计时光机服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditTimeMachineServiceImpl implements AuditTimeMachineService {

    private final PermAuditSnapshotMapper auditSnapshotMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void createSnapshot(Long userId, String changeType, String changeReason) {
        // TODO: 获取用户当前权限状态
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();
        String dataScope = "3";

        PermAuditSnapshot snapshot = new PermAuditSnapshot();
        snapshot.setUserId(userId);
        snapshot.setSnapshotTime(LocalDateTime.now());
        snapshot.setChangeType(changeType);
        snapshot.setChangeReason(changeReason);

        try {
            snapshot.setRoles(objectMapper.writeValueAsString(roles));
            snapshot.setPermissions(objectMapper.writeValueAsString(permissions));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize snapshot data", e);
        }

        snapshot.setDataScope(dataScope);
        snapshot.setCreatedAt(LocalDateTime.now());

        auditSnapshotMapper.insert(snapshot);
        log.info("Created audit snapshot for user: {}", userId);
    }

    @Override
    public List<PermAuditSnapshot> getSnapshots(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<PermAuditSnapshot> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (startTime != null) {
            wrapper.ge("snapshot_time", startTime);
        }
        if (endTime != null) {
            wrapper.le("snapshot_time", endTime);
        }
        wrapper.orderByDesc("snapshot_time");
        return auditSnapshotMapper.selectList(wrapper);
    }

    @Override
    public PermAuditSnapshot timeTravel(Long userId, LocalDateTime timestamp) {
        // 查找指定时间点之前最近的快照
        return auditSnapshotMapper.selectOne(new QueryWrapper<PermAuditSnapshot>()
                .eq("user_id", userId)
                .le("snapshot_time", timestamp)
                .orderByDesc("snapshot_time")
                .last("LIMIT 1"));
    }

    @Override
    public List<Map<String, Object>> getTimeline(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<PermAuditSnapshot> snapshots = getSnapshots(userId, startTime, endTime);

        List<Map<String, Object>> timeline = new ArrayList<>();
        for (PermAuditSnapshot snapshot : snapshots) {
            Map<String, Object> event = new HashMap<>();
            event.put("snapshotId", snapshot.getAuditId());
            event.put("snapshotTime", snapshot.getSnapshotTime());
            event.put("changeType", snapshot.getChangeType());
            event.put("changeReason", snapshot.getChangeReason());
            timeline.add(event);
        }

        return timeline;
    }

    @Override
    public Map<String, Object> compare(Long userId, LocalDateTime timestamp1, LocalDateTime timestamp2) {
        PermAuditSnapshot snapshot1 = timeTravel(userId, timestamp1);
        PermAuditSnapshot snapshot2 = timeTravel(userId, timestamp2);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("timestamp1", timestamp1);
        result.put("timestamp2", timestamp2);

        Map<String, Object> diff = new HashMap<>();

        if (snapshot1 != null && snapshot2 != null) {
            try {
                // 对比角色变化
                List<String> roles1 = objectMapper.readValue(snapshot1.getRoles(), List.class);
                List<String> roles2 = objectMapper.readValue(snapshot2.getRoles(), List.class);

                List<String> addedRoles = new ArrayList<>(roles2);
                addedRoles.removeAll(roles1);

                List<String> removedRoles = new ArrayList<>(roles1);
                removedRoles.removeAll(roles2);

                diff.put("addedRoles", addedRoles);
                diff.put("removedRoles", removedRoles);

                // 对比权限变化
                List<String> perms1 = objectMapper.readValue(snapshot1.getPermissions(), List.class);
                List<String> perms2 = objectMapper.readValue(snapshot2.getPermissions(), List.class);

                List<String> addedPerms = new ArrayList<>(perms2);
                addedPerms.removeAll(perms1);

                List<String> removedPerms = new ArrayList<>(perms1);
                removedPerms.removeAll(perms2);

                diff.put("addedPermissions", addedPerms);
                diff.put("removedPermissions", removedPerms);

            } catch (JsonProcessingException e) {
                log.error("Failed to parse snapshot data", e);
            }
        }

        result.put("diff", diff);
        result.put("snapshot1", snapshot1);
        result.put("snapshot2", snapshot2);

        return result;
    }

    @Override
    public Map<String, Object> simulateWhatIf(Long userId, String changeType, Map<String, Object> changeData) {
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("changeType", changeType);
        result.put("changeData", changeData);

        // TODO: 实现模拟逻辑
        List<Long> affectedUsers = new ArrayList<>();
        affectedUsers.add(userId);

        result.put("affectedUsers", affectedUsers);
        result.put("impactLevel", "LOW");
        result.put("description", "模拟变更影响分析");

        return result;
    }
}

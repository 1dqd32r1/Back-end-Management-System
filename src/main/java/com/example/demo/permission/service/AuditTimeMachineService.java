package com.example.demo.permission.service;

import com.example.demo.permission.entity.PermAuditSnapshot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 权限审计时光机服务接口
 */
public interface AuditTimeMachineService {

    /**
     * 创建权限快照
     */
    void createSnapshot(Long userId, String changeType, String changeReason);

    /**
     * 获取用户权限快照列表
     */
    List<PermAuditSnapshot> getSnapshots(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 时间旅行 - 回放历史权限状态
     */
    PermAuditSnapshot timeTravel(Long userId, LocalDateTime timestamp);

    /**
     * 权限变更时间线
     */
    List<Map<String, Object>> getTimeline(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 对比两个时间点的权限差异
     */
    Map<String, Object> compare(Long userId, LocalDateTime timestamp1, LocalDateTime timestamp2);

    /**
     * What-If 模拟 - 模拟权限变更影响
     */
    Map<String, Object> simulateWhatIf(Long userId, String changeType, Map<String, Object> changeData);
}

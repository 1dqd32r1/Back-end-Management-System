package com.example.demo.permission.controller;

import com.example.demo.common.Result;
import com.example.demo.permission.entity.PermAuditSnapshot;
import com.example.demo.permission.service.AuditTimeMachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 审计时光机控制器
 */
@RestController
@RequestMapping("/api/permission/audit")
@RequiredArgsConstructor
public class AuditTimeMachineController {

    private final AuditTimeMachineService auditTimeMachineService;

    /**
     * 获取用户权限快照列表
     */
    @GetMapping("/snapshot/{userId}")
    public Result<List<PermAuditSnapshot>> getSnapshots(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(auditTimeMachineService.getSnapshots(userId, startTime, endTime));
    }

    /**
     * 获取权限变更时间线
     */
    @GetMapping("/timeline/{userId}")
    public Result<List<Map<String, Object>>> getTimeline(
            @PathVariable Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return Result.success(auditTimeMachineService.getTimeline(userId, startTime, endTime));
    }

    /**
     * 权限状态回放（时间旅行）
     */
    @PostMapping("/replay")
    public Result<PermAuditSnapshot> replay(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        LocalDateTime timestamp = LocalDateTime.parse((String) params.get("timestamp"));
        return Result.success(auditTimeMachineService.timeTravel(userId, timestamp));
    }

    /**
     * 权限状态对比
     */
    @PostMapping("/compare")
    public Result<Map<String, Object>> compare(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        LocalDateTime timestamp1 = LocalDateTime.parse((String) params.get("timestamp1"));
        LocalDateTime timestamp2 = LocalDateTime.parse((String) params.get("timestamp2"));
        return Result.success(auditTimeMachineService.compare(userId, timestamp1, timestamp2));
    }

    /**
     * What-If 模拟
     */
    @PostMapping("/what-if")
    public Result<Map<String, Object>> whatIf(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String changeType = (String) params.get("changeType");
        @SuppressWarnings("unchecked")
        Map<String, Object> changeData = (Map<String, Object>) params.get("changeData");
        return Result.success(auditTimeMachineService.simulateWhatIf(userId, changeType, changeData));
    }

    /**
     * 创建快照
     */
    @PostMapping("/snapshot/create")
    public Result<?> createSnapshot(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String changeType = (String) params.get("changeType");
        String changeReason = (String) params.get("changeReason");
        auditTimeMachineService.createSnapshot(userId, changeType, changeReason);
        return Result.success();
    }
}

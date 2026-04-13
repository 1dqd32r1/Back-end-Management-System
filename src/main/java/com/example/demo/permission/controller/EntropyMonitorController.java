package com.example.demo.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.permission.entity.PermEntropyMonitor;
import com.example.demo.permission.mapper.PermEntropyMonitorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限熵值监控控制器
 */
@RestController
@RequestMapping("/api/permission/entropy")
@RequiredArgsConstructor
public class EntropyMonitorController {

    private final PermEntropyMonitorMapper entropyMonitorMapper;

    /**
     * 获取用户权限熵值
     */
    @GetMapping("/{userId}")
    public Result<PermEntropyMonitor> getEntropy(@PathVariable Long userId) {
        PermEntropyMonitor monitor = entropyMonitorMapper.selectOne(
                new QueryWrapper<PermEntropyMonitor>()
                        .eq("user_id", userId)
                        .orderByDesc("period_start")
                        .last("LIMIT 1"));
        return Result.success(monitor);
    }

    /**
     * 获取熵值历史
     */
    @GetMapping("/{userId}/history")
    public Result<List<PermEntropyMonitor>> getHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") Integer days) {

        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<PermEntropyMonitor> monitors = entropyMonitorMapper.selectList(
                new QueryWrapper<PermEntropyMonitor>()
                        .eq("user_id", userId)
                        .ge("period_start", startDate)
                        .orderByDesc("period_start"));

        return Result.success(monitors);
    }

    /**
     * 权限漂移报告
     */
    @GetMapping("/drift-report")
    public Result<Page<Map<String, Object>>> getDriftReport(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "50") Double threshold) {

        Page<PermEntropyMonitor> page = new Page<>(pageNum, pageSize);
        entropyMonitorMapper.selectPage(page,
                new QueryWrapper<PermEntropyMonitor>()
                        .ge("drift_score", threshold)
                        .orderByDesc("drift_score"));

        // 转换为报告格式
        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setTotal(page.getTotal());
        resultPage.setRecords(page.getRecords().stream().map(m -> {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", m.getUserId());
            map.put("driftScore", m.getDriftScore());
            map.put("permissionEntropy", m.getPermissionEntropy());
            map.put("unusedPermissions", m.getUnusedPermissions());
            map.put("recommendedRevocations", m.getRecommendedRevocations());
            map.put("periodStart", m.getPeriodStart());
            map.put("periodEnd", m.getPeriodEnd());
            return map;
        }).toList());

        return Result.success(resultPage);
    }

    /**
     * 生成权限回收建议
     */
    @PostMapping("/recommend-revocation")
    public Result<Map<String, Object>> recommendRevocation(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());

        PermEntropyMonitor monitor = entropyMonitorMapper.selectOne(
                new QueryWrapper<PermEntropyMonitor>()
                        .eq("user_id", userId)
                        .orderByDesc("period_start")
                        .last("LIMIT 1"));

        Map<String, Object> result = new HashMap<>();
        if (monitor != null) {
            result.put("userId", userId);
            result.put("driftScore", monitor.getDriftScore());
            result.put("unusedPermissions", monitor.getUnusedPermissions());
            result.put("recommendedRevocations", monitor.getRecommendedRevocations());
            result.put("recommendation", "建议回收" + monitor.getUnusedPermissions() + "项未使用权限");
        } else {
            result.put("userId", userId);
            result.put("recommendation", "暂无足够数据生成建议");
        }

        return Result.success(result);
    }
}

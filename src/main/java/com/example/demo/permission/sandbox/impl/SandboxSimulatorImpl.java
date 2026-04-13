package com.example.demo.permission.sandbox.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.permission.context.DecisionType;
import com.example.demo.permission.entity.*;
import com.example.demo.permission.mapper.*;
import com.example.demo.permission.sandbox.SandboxSimulator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限沙盒模拟器实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SandboxSimulatorImpl implements SandboxSimulator {

    private final PermSandboxSimulationMapper simulationMapper;
    private final PermAuditSnapshotMapper auditSnapshotMapper;
    private final PermDynamicRuleMapper ruleMapper;
    private final PermDecisionLogMapper decisionLogMapper;
    private final PermUserProfileMapper userProfileMapper;
    private final ObjectMapper objectMapper;

    @Override
    public SimulationResult executeWhatIf(WhatIfParams params) {
        long startTime = System.currentTimeMillis();

        PermSandboxSimulation simulation = new PermSandboxSimulation();
        simulation.setSimulationName(params.simulationName());
        simulation.setSimulationType("WHAT_IF");
        simulation.setExecutedBy(params.executedBy());
        simulation.setExecutionTime(LocalDateTime.now());

        try {
            // 构建变量覆盖配置
            Map<String, Object> overrides = new HashMap<>();
            overrides.put("changeType", params.changeType());
            overrides.put("changeData", params.changeData());
            overrides.put("targetUserId", params.userId());
            simulation.setVariableOverrides(toJson(overrides));

            // 模拟变更影响
            List<AffectedUser> affectedUsers = simulateChange(params);

            // 生成热力图
            Map<String, Object> heatmap = generateHeatmapData(affectedUsers);

            // 预测冲突
            List<ConflictPrediction> conflicts = predictConflicts(params, affectedUsers);

            // 保存结果
            simulation.setAffectedUsers(toJson(affectedUsers.stream().map(this::toMap).collect(Collectors.toList())));
            simulation.setImpactHeatmap(toJson(heatmap));
            simulation.setConflictPredictions(toJson(conflicts.stream().map(this::conflictToMap).collect(Collectors.toList())));
            simulation.setExecutionDurationMs((int) (System.currentTimeMillis() - startTime));
            simulationMapper.insert(simulation);

            return new SimulationResult(
                    simulation.getSimulationId(),
                    "WHAT_IF",
                    true,
                    "模拟执行成功",
                    affectedUsers,
                    heatmap,
                    conflicts,
                    simulation.getExecutionDurationMs()
            );

        } catch (Exception e) {
            log.error("What-If simulation failed", e);
            simulation.setExecutionDurationMs((int) (System.currentTimeMillis() - startTime));
            simulationMapper.insert(simulation);

            return new SimulationResult(
                    null,
                    "WHAT_IF",
                    false,
                    "模拟执行失败: " + e.getMessage(),
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    simulation.getExecutionDurationMs()
            );
        }
    }

    @Override
    public SimulationResult executeTimeTravel(TimeTravelParams params) {
        long startTime = System.currentTimeMillis();

        PermSandboxSimulation simulation = new PermSandboxSimulation();
        simulation.setSimulationName(params.simulationName());
        simulation.setSimulationType("TIME_TRAVEL");
        simulation.setTargetTimestamp(LocalDateTime.parse(params.targetTimestamp().replace(" ", "T")));
        simulation.setExecutedBy(params.executedBy());
        simulation.setExecutionTime(LocalDateTime.now());

        try {
            // 查找目标时间的快照
            List<PermAuditSnapshot> snapshots = auditSnapshotMapper.selectList(
                    new QueryWrapper<PermAuditSnapshot>()
                            .eq("user_id", params.userId())
                            .le("snapshot_time", simulation.getTargetTimestamp())
                            .orderByDesc("snapshot_time")
                            .last("LIMIT 5")
            );

            List<AffectedUser> affectedUsers = new ArrayList<>();
            if (!snapshots.isEmpty()) {
                PermAuditSnapshot snapshot = snapshots.get(0);
                AffectedUser user = new AffectedUser(
                        params.userId(),
                        "User" + params.userId(),
                        parseJsonList(snapshot.getPermissions()),
                        Collections.emptyList(),
                        DecisionType.ALLOW,
                        DecisionType.ALLOW
                );
                affectedUsers.add(user);
            }

            Map<String, Object> heatmap = generateHeatmapData(affectedUsers);
            List<ConflictPrediction> conflicts = Collections.emptyList();

            simulation.setAffectedUsers(toJson(affectedUsers.stream().map(this::toMap).collect(Collectors.toList())));
            simulation.setImpactHeatmap(toJson(heatmap));
            simulation.setConflictPredictions(toJson(conflicts));
            simulation.setExecutionDurationMs((int) (System.currentTimeMillis() - startTime));
            simulationMapper.insert(simulation);

            return new SimulationResult(
                    simulation.getSimulationId(),
                    "TIME_TRAVEL",
                    true,
                    "时光机模拟成功，回溯到 " + params.targetTimestamp(),
                    affectedUsers,
                    heatmap,
                    conflicts,
                    simulation.getExecutionDurationMs()
            );

        } catch (Exception e) {
            log.error("Time travel simulation failed", e);
            simulation.setExecutionDurationMs((int) (System.currentTimeMillis() - startTime));
            simulationMapper.insert(simulation);

            return new SimulationResult(
                    null,
                    "TIME_TRAVEL",
                    false,
                    "模拟执行失败: " + e.getMessage(),
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    simulation.getExecutionDurationMs()
            );
        }
    }

    @Override
    public SimulationResult executeStressTest(StressTestParams params) {
        long startTime = System.currentTimeMillis();

        PermSandboxSimulation simulation = new PermSandboxSimulation();
        simulation.setSimulationName(params.simulationName());
        simulation.setSimulationType("STRESS");
        simulation.setExecutedBy(params.executedBy());
        simulation.setExecutionTime(LocalDateTime.now());

        try {
            Map<String, Object> overrides = new HashMap<>();
            overrides.put("concurrentUsers", params.concurrentUsers());
            overrides.put("requestsPerSecond", params.requestsPerSecond());
            overrides.put("durationSeconds", params.durationSeconds());
            simulation.setVariableOverrides(toJson(overrides));

            // 模拟压力测试结果
            Random random = new Random();
            List<AffectedUser> affectedUsers = new ArrayList<>();
            for (int i = 1; i <= Math.min(params.concurrentUsers(), 10); i++) {
                affectedUsers.add(new AffectedUser(
                        (long) i,
                        "User" + i,
                        Collections.singletonList("stress_test_perm"),
                        Collections.emptyList(),
                        DecisionType.ALLOW,
                        random.nextDouble() > 0.1 ? DecisionType.ALLOW : DecisionType.DENY
                ));
            }

            Map<String, Object> heatmap = new HashMap<>();
            heatmap.put("totalRequests", params.concurrentUsers() * params.requestsPerSecond() * params.durationSeconds());
            heatmap.put("successRate", 95.0 + random.nextDouble() * 4);
            heatmap.put("avgLatencyMs", 15 + random.nextDouble() * 25);
            heatmap.put("p99LatencyMs", 35 + random.nextDouble() * 15);

            List<ConflictPrediction> conflicts = new ArrayList<>();
            if (random.nextDouble() > 0.7) {
                conflicts.add(new ConflictPrediction(
                        "高并发规则冲突",
                        "CONCURRENT_ACCESS",
                        "在高并发场景下可能触发频率限制",
                        Collections.singletonList(1L)
                ));
            }

            simulation.setAffectedUsers(toJson(affectedUsers.stream().map(this::toMap).collect(Collectors.toList())));
            simulation.setImpactHeatmap(toJson(heatmap));
            simulation.setConflictPredictions(toJson(conflicts.stream().map(this::conflictToMap).collect(Collectors.toList())));
            simulation.setExecutionDurationMs((int) (System.currentTimeMillis() - startTime));
            simulationMapper.insert(simulation);

            return new SimulationResult(
                    simulation.getSimulationId(),
                    "STRESS",
                    true,
                    String.format("压力测试完成: %d用户, %d req/s, %ds", params.concurrentUsers(), params.requestsPerSecond(), params.durationSeconds()),
                    affectedUsers,
                    heatmap,
                    conflicts,
                    simulation.getExecutionDurationMs()
            );

        } catch (Exception e) {
            log.error("Stress test simulation failed", e);
            simulation.setExecutionDurationMs((int) (System.currentTimeMillis() - startTime));
            simulationMapper.insert(simulation);

            return new SimulationResult(
                    null,
                    "STRESS",
                    false,
                    "模拟执行失败: " + e.getMessage(),
                    Collections.emptyList(),
                    Collections.emptyMap(),
                    Collections.emptyList(),
                    simulation.getExecutionDurationMs()
            );
        }
    }

    @Override
    public List<PermSandboxSimulation> getSimulationHistory(Long executedBy, int limit) {
        return simulationMapper.selectList(
                new QueryWrapper<PermSandboxSimulation>()
                        .eq(executedBy != null, "executed_by", executedBy)
                        .orderByDesc("execution_time")
                        .last("LIMIT " + limit)
        );
    }

    @Override
    public Map<String, Object> generateImpactHeatmap(Long simulationId) {
        PermSandboxSimulation simulation = simulationMapper.selectById(simulationId);
        if (simulation == null || simulation.getImpactHeatmap() == null) {
            return Collections.emptyMap();
        }
        return parseJsonMap(simulation.getImpactHeatmap());
    }

    // ==================== 私有方法 ====================

    private List<AffectedUser> simulateChange(WhatIfParams params) {
        List<AffectedUser> affectedUsers = new ArrayList<>();
        Random random = new Random();

        // 模拟受影响用户
        int affectedCount = switch (params.changeType()) {
            case "addRole", "addPermission" -> 1 + random.nextInt(5);
            case "removeRole", "removePermission" -> 1 + random.nextInt(3);
            default -> 1;
        };

        for (int i = 0; i < affectedCount; i++) {
            Long userId = i == 0 ? params.userId() : params.userId() + i + 1;

            List<String> addedPerms = new ArrayList<>();
            List<String> removedPerms = new ArrayList<>();

            if (params.changeType().equals("addRole")) {
                addedPerms.add("role:" + params.changeData().get("addRole"));
                addedPerms.addAll(generateRandomPermissions(2 + random.nextInt(3)));
            } else if (params.changeType().equals("removeRole")) {
                removedPerms.add("role:" + params.changeData().get("removeRole"));
                removedPerms.addAll(generateRandomPermissions(1 + random.nextInt(2)));
            } else if (params.changeType().equals("addPermission")) {
                addedPerms.add(params.changeData().get("addPermission").toString());
            } else if (params.changeType().equals("removePermission")) {
                removedPerms.add(params.changeData().get("removePermission").toString());
            }

            affectedUsers.add(new AffectedUser(
                    userId,
                    "User" + userId,
                    addedPerms,
                    removedPerms,
                    DecisionType.ALLOW,
                    random.nextDouble() > 0.2 ? DecisionType.ALLOW : DecisionType.CONDITIONAL
            ));
        }

        return affectedUsers;
    }

    private List<String> generateRandomPermissions(int count) {
        String[] perms = {"user:view", "user:add", "user:edit", "dept:view", "role:view", "menu:view", "log:view"};
        List<String> result = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            result.add(perms[random.nextInt(perms.length)]);
        }
        return result;
    }

    private Map<String, Object> generateHeatmapData(List<AffectedUser> affectedUsers) {
        Map<String, Object> heatmap = new HashMap<>();
        Map<String, Integer> permissionCount = new HashMap<>();
        Map<String, Integer> userImpact = new HashMap<>();

        for (AffectedUser user : affectedUsers) {
            String key = "user_" + user.userId();
            userImpact.put(key, user.addedPermissions().size() + user.removedPermissions().size());

            for (String perm : user.addedPermissions()) {
                permissionCount.merge(perm, 1, Integer::sum);
            }
            for (String perm : user.removedPermissions()) {
                permissionCount.merge(perm + "_removed", 1, Integer::sum);
            }
        }

        heatmap.put("permissionDistribution", permissionCount);
        heatmap.put("userImpactLevel", userImpact);
        heatmap.put("totalAffectedUsers", affectedUsers.size());

        return heatmap;
    }

    private List<ConflictPrediction> predictConflicts(WhatIfParams params, List<AffectedUser> affectedUsers) {
        List<ConflictPrediction> predictions = new ArrayList<>();

        // 检查规则冲突
        List<PermDynamicRule> activeRules = ruleMapper.selectList(
                new QueryWrapper<PermDynamicRule>().eq("status", 1)
        );

        for (PermDynamicRule rule : activeRules) {
            if (params.changeType().contains("add") && rule.getActionType().equals("DENY")) {
                predictions.add(new ConflictPrediction(
                        rule.getRuleName(),
                        "RULE_CONFLICT",
                        "新增权限可能与规则【" + rule.getRuleName() + "】产生冲突",
                        affectedUsers.stream().map(AffectedUser::userId).collect(Collectors.toList())
                ));
            }
        }

        // 模拟检测越权风险
        if (params.changeType().equals("addRole") && affectedUsers.size() > 3) {
            predictions.add(new ConflictPrediction(
                    "批量授权风险",
                    "OVER_PERMISSION",
                    "批量添加角色可能导致权限扩散",
                    affectedUsers.stream().map(AffectedUser::userId).limit(3).collect(Collectors.toList())
            ));
        }

        return predictions;
    }

    private Map<String, Object> toMap(AffectedUser user) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.userId());
        map.put("userName", user.userName());
        map.put("addedPermissions", user.addedPermissions());
        map.put("removedPermissions", user.removedPermissions());
        map.put("beforeDecision", user.beforeDecision());
        map.put("afterDecision", user.afterDecision());
        return map;
    }

    private Map<String, Object> conflictToMap(ConflictPrediction conflict) {
        Map<String, Object> map = new HashMap<>();
        map.put("ruleName", conflict.ruleName());
        map.put("conflictType", conflict.conflictType());
        map.put("description", conflict.description());
        map.put("involvedUsers", conflict.involvedUsers());
        return map;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private List<String> parseJsonList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<String, Object> parseJsonMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}

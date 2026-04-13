package com.example.demo.permission.sandbox;

import com.example.demo.permission.context.DecisionType;
import com.example.demo.permission.entity.PermSandboxSimulation;

import java.util.List;
import java.util.Map;

/**
 * 权限沙盒模拟器接口
 */
public interface SandboxSimulator {

    /**
     * 执行What-If模拟
     * @param params 模拟参数
     * @return 模拟结果
     */
    SimulationResult executeWhatIf(WhatIfParams params);

    /**
     * 执行时光机模拟
     * @param params 模拟参数
     * @return 模拟结果
     */
    SimulationResult executeTimeTravel(TimeTravelParams params);

    /**
     * 执行压力测试模拟
     * @param params 模拟参数
     * @return 模拟结果
     */
    SimulationResult executeStressTest(StressTestParams params);

    /**
     * 获取模拟历史
     * @param executedBy 执行人
     * @param limit 限制数量
     * @return 模拟记录列表
     */
    List<PermSandboxSimulation> getSimulationHistory(Long executedBy, int limit);

    /**
     * 生成影响面热力图
     * @param simulationId 模拟ID
     * @return 热力图数据
     */
    Map<String, Object> generateImpactHeatmap(Long simulationId);

    // ==================== 参数和结果类 ====================

    record WhatIfParams(
            String simulationName,
            Long userId,
            String changeType,  // addRole, removeRole, addPermission, removePermission
            Map<String, Object> changeData,
            Long executedBy
    ) {}

    record TimeTravelParams(
            String simulationName,
            Long userId,
            String targetTimestamp,
            Long executedBy
    ) {}

    record StressTestParams(
            String simulationName,
            int concurrentUsers,
            int requestsPerSecond,
            int durationSeconds,
            Long executedBy
    ) {}

    record SimulationResult(
            Long simulationId,
            String simulationType,
            boolean success,
            String message,
            List<AffectedUser> affectedUsers,
            Map<String, Object> impactHeatmap,
            List<ConflictPrediction> conflictPredictions,
            int executionDurationMs
    ) {}

    record AffectedUser(
            Long userId,
            String userName,
            List<String> addedPermissions,
            List<String> removedPermissions,
            DecisionType beforeDecision,
            DecisionType afterDecision
    ) {}

    record ConflictPrediction(
            String ruleName,
            String conflictType,
            String description,
            List<Long> involvedUsers
    ) {}
}

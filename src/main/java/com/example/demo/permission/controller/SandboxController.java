package com.example.demo.permission.controller;

import com.example.demo.common.Result;
import com.example.demo.permission.entity.PermSandboxSimulation;
import com.example.demo.permission.sandbox.SandboxSimulator;
import com.example.demo.permission.sandbox.impl.SandboxSimulatorImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限沙盒模拟控制器
 */
@RestController
@RequestMapping("/api/permission/sandbox")
@RequiredArgsConstructor
public class SandboxController {

    private final SandboxSimulator sandboxSimulator;

    /**
     * 执行What-If模拟
     */
    @PostMapping("/what-if")
    public Result<SandboxSimulator.SimulationResult> executeWhatIf(@RequestBody Map<String, Object> params) {
        SandboxSimulator.WhatIfParams whatIfParams = new SandboxSimulator.WhatIfParams(
                (String) params.getOrDefault("simulationName", "What-If模拟"),
                Long.valueOf(params.get("userId").toString()),
                (String) params.get("changeType"),
                (Map<String, Object>) params.get("changeData"),
                params.containsKey("executedBy") ? Long.valueOf(params.get("executedBy").toString()) : 1L
        );

        SandboxSimulator.SimulationResult result = sandboxSimulator.executeWhatIf(whatIfParams);
        return Result.success(result);
    }

    /**
     * 执行时光机模拟
     */
    @PostMapping("/time-travel")
    public Result<SandboxSimulator.SimulationResult> executeTimeTravel(@RequestBody Map<String, Object> params) {
        SandboxSimulator.TimeTravelParams timeTravelParams = new SandboxSimulator.TimeTravelParams(
                (String) params.getOrDefault("simulationName", "时光机模拟"),
                Long.valueOf(params.get("userId").toString()),
                (String) params.get("targetTimestamp"),
                params.containsKey("executedBy") ? Long.valueOf(params.get("executedBy").toString()) : 1L
        );

        SandboxSimulator.SimulationResult result = sandboxSimulator.executeTimeTravel(timeTravelParams);
        return Result.success(result);
    }

    /**
     * 执行压力测试
     */
    @PostMapping("/stress-test")
    public Result<SandboxSimulator.SimulationResult> executeStressTest(@RequestBody Map<String, Object> params) {
        SandboxSimulator.StressTestParams stressTestParams = new SandboxSimulator.StressTestParams(
                (String) params.getOrDefault("simulationName", "压力测试"),
                Integer.parseInt(params.getOrDefault("concurrentUsers", 100).toString()),
                Integer.parseInt(params.getOrDefault("requestsPerSecond", 1000).toString()),
                Integer.parseInt(params.getOrDefault("durationSeconds", 60).toString()),
                params.containsKey("executedBy") ? Long.valueOf(params.get("executedBy").toString()) : 1L
        );

        SandboxSimulator.SimulationResult result = sandboxSimulator.executeStressTest(stressTestParams);
        return Result.success(result);
    }

    /**
     * 获取模拟历史
     */
    @GetMapping("/history")
    public Result<List<PermSandboxSimulation>> getHistory(
            @RequestParam(required = false) Long executedBy,
            @RequestParam(defaultValue = "20") Integer limit) {

        List<PermSandboxSimulation> history = sandboxSimulator.getSimulationHistory(executedBy, limit);
        return Result.success(history);
    }

    /**
     * 获取影响面热力图
     */
    @GetMapping("/heatmap/{simulationId}")
    public Result<Map<String, Object>> getHeatmap(@PathVariable Long simulationId) {
        Map<String, Object> heatmap = sandboxSimulator.generateImpactHeatmap(simulationId);
        return Result.success(heatmap);
    }
}

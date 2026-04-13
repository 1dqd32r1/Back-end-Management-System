package com.example.demo.permission.controller;

import com.example.demo.common.Result;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;
import com.example.demo.permission.service.PermissionDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限决策控制器
 */
@RestController
@RequestMapping("/api/permission/decision")
@RequiredArgsConstructor
public class PermissionDecisionController {

    private final PermissionDecisionService decisionService;

    /**
     * 实时权限检查
     */
    @PostMapping("/check")
    public Result<DecisionResult> check(@RequestBody DecisionRequest request) {
        DecisionResult result = decisionService.check(request);
        return Result.success(result);
    }

    /**
     * 批量权限检查
     */
    @PostMapping("/batch")
    public Result<List<DecisionResult>> batchCheck(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> checks = (List<Map<String, Object>>) params.get("checks");
        Long userId = Long.valueOf(params.get("userId").toString());

        List<DecisionRequest> requests = checks.stream()
                .map(check -> DecisionRequest.builder()
                        .userId(userId)
                        .resourceType((String) check.get("resourceType"))
                        .resourceId((String) check.get("resourceId"))
                        .operation((String) check.get("operation"))
                        .build())
                .toList();

        List<DecisionResult> results = decisionService.batchCheck(requests);
        return Result.success(results);
    }

    /**
     * 获取决策解释
     */
    @GetMapping("/explain/{requestId}")
    public Result<DecisionResult> explain(@PathVariable String requestId) {
        DecisionResult result = decisionService.explain(requestId);
        return Result.success(result);
    }
}

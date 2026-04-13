package com.example.demo.permission.core;

import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 权限决策引擎接口
 */
public interface PermissionDecisionEngine {

    /**
     * 实时权限决策
     *
     * @param request 权限决策请求
     * @return 权限决策结果
     */
    DecisionResult decide(DecisionRequest request);

    /**
     * 批量权限决策
     *
     * @param requests 权限决策请求列表
     * @return 权限决策结果列表
     */
    List<DecisionResult> batchDecide(List<DecisionRequest> requests);

    /**
     * 异步权限决策
     *
     * @param request 权限决策请求
     * @return 异步决策结果
     */
    CompletableFuture<DecisionResult> decideAsync(DecisionRequest request);
}

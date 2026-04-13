package com.example.demo.permission.service;

import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;

import java.util.List;

/**
 * 权限决策服务接口
 */
public interface PermissionDecisionService {

    /**
     * 实时权限检查
     */
    DecisionResult check(DecisionRequest request);

    /**
     * 批量权限检查
     */
    List<DecisionResult> batchCheck(List<DecisionRequest> requests);

    /**
     * 获取决策解释
     */
    DecisionResult explain(String requestId);
}

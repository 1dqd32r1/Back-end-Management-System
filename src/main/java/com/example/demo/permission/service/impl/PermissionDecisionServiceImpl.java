package com.example.demo.permission.service.impl;

import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;
import com.example.demo.permission.core.PermissionDecisionEngine;
import com.example.demo.permission.service.PermissionDecisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限决策服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionDecisionServiceImpl implements PermissionDecisionService {

    private final PermissionDecisionEngine decisionEngine;

    @Override
    public DecisionResult check(DecisionRequest request) {
        log.debug("Permission check request: userId={}, resource={}, operation={}",
                request.getUserId(), request.getResourceType(), request.getOperation());
        return decisionEngine.decide(request);
    }

    @Override
    public List<DecisionResult> batchCheck(List<DecisionRequest> requests) {
        return decisionEngine.batchDecide(requests);
    }

    @Override
    public DecisionResult explain(String requestId) {
        // TODO: 从数据库查询决策日志并生成解释
        return DecisionResult.builder()
                .requestId(requestId)
                .decision(com.example.demo.permission.context.DecisionType.ALLOW)
                .explanation("权限检查通过")
                .build();
    }
}

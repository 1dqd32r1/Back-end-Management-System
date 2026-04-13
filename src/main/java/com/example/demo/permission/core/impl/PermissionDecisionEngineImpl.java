package com.example.demo.permission.core.impl;

import com.example.demo.permission.context.*;
import com.example.demo.permission.core.PermissionDecisionEngine;
import com.example.demo.permission.entity.PermDynamicRule;
import com.example.demo.permission.mapper.PermDynamicRuleMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 权限决策引擎实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionDecisionEngineImpl implements PermissionDecisionEngine {

    private final PermDynamicRuleMapper ruleMapper;

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public DecisionResult decide(DecisionRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            // 获取上下文快照
            ContextSnapshot snapshot = ThreadLocalContextHolder.get();
            if (snapshot == null) {
                log.warn("No context snapshot found for request: {}", request.getRequestId());
                return DecisionResult.deny(request.getRequestId(), "上下文信息缺失");
            }

            // 获取启用的规则
            List<PermDynamicRule> rules = ruleMapper.selectEnabledRulesByPriority();

            // 评估规则
            List<String> matchedRules = new ArrayList<>();
            DecisionType finalDecision = DecisionType.ALLOW;
            String denialReason = null;

            StandardEvaluationContext evalContext = createEvaluationContext(snapshot, request);

            for (PermDynamicRule rule : rules) {
                try {
                    Boolean matches = expressionParser.parseExpression(rule.getConditionExpression())
                            .getValue(evalContext, Boolean.class);

                    if (Boolean.TRUE.equals(matches)) {
                        matchedRules.add(rule.getRuleCode());

                        // 根据动作类型确定决策
                        DecisionType ruleDecision = mapActionToDecision(rule.getActionType());

                        // 规则优先级处理：DENY > CONDITIONAL > ALLOW
                        if (ruleDecision == DecisionType.DENY) {
                            finalDecision = DecisionType.DENY;
                            denialReason = rule.getDescription();
                            break; // DENY规则直接终止
                        } else if (ruleDecision == DecisionType.CONDITIONAL && finalDecision == DecisionType.ALLOW) {
                            finalDecision = DecisionType.CONDITIONAL;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Rule evaluation failed for rule: {}", rule.getRuleCode(), e);
                }
            }

            long latency = System.currentTimeMillis() - startTime;

            DecisionResult result = DecisionResult.builder()
                    .requestId(request.getRequestId())
                    .decision(finalDecision)
                    .matchedRules(matchedRules)
                    .denialReason(denialReason)
                    .latencyMs(latency)
                    .build();

            // 生成解释
            if (finalDecision == DecisionType.DENY) {
                result.setExplanation(generateExplanation(snapshot, request, denialReason));
            }

            log.debug("Decision completed in {}ms: {} for request {}", latency, finalDecision, request.getRequestId());

            return result;

        } catch (Exception e) {
            log.error("Decision engine error", e);
            return DecisionResult.deny(request.getRequestId(), "权限决策异常: " + e.getMessage());
        }
    }

    @Override
    public List<DecisionResult> batchDecide(List<DecisionRequest> requests) {
        List<DecisionResult> results = new ArrayList<>();
        for (DecisionRequest request : requests) {
            results.add(decide(request));
        }
        return results;
    }

    @Override
    public CompletableFuture<DecisionResult> decideAsync(DecisionRequest request) {
        return CompletableFuture.supplyAsync(() -> decide(request));
    }

    /**
     * 创建SpEL评估上下文
     */
    private StandardEvaluationContext createEvaluationContext(ContextSnapshot snapshot, DecisionRequest request) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        // 设置变量
        context.setVariable("userId", snapshot.getUserId());
        context.setVariable("sessionId", snapshot.getSessionId());
        context.setVariable("timestamp", snapshot.getTimestamp());
        context.setVariable("resourceType", request.getResourceType());
        context.setVariable("resourceId", request.getResourceId());
        context.setVariable("operation", request.getOperation());

        // 设置维度对象
        if (snapshot.getOrganizational() != null) {
            context.setVariable("org", snapshot.getOrganizational());
        }
        if (snapshot.getSpatiotemporal() != null) {
            context.setVariable("time", snapshot.getSpatiotemporal());
        }
        if (snapshot.getRisk() != null) {
            context.setVariable("risk", snapshot.getRisk());
        }
        if (snapshot.getBehavioral() != null) {
            context.setVariable("behavior", snapshot.getBehavioral());
        }

        return context;
    }

    /**
     * 映射动作类型到决策类型
     */
    private DecisionType mapActionToDecision(String actionType) {
        return switch (actionType.toUpperCase()) {
            case "ALLOW" -> DecisionType.ALLOW;
            case "DENY" -> DecisionType.DENY;
            case "ELEVATE", "DEGRADE" -> DecisionType.CONDITIONAL;
            default -> DecisionType.ALLOW;
        };
    }

    /**
     * 生成决策解释
     */
    private String generateExplanation(ContextSnapshot snapshot, DecisionRequest request, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("您的").append(request.getOperation()).append("操作被拒绝。");
        if (reason != null) {
            sb.append("原因：").append(reason);
        }
        if (snapshot.getSpatiotemporal() != null && !snapshot.getSpatiotemporal().getIsWorkingHours()) {
            sb.append(" 当前不在工作时间范围内。");
        }
        return sb.toString();
    }
}

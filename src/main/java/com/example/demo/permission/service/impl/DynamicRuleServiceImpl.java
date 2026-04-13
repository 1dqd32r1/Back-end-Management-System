package com.example.demo.permission.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;
import com.example.demo.permission.entity.PermDynamicRule;
import com.example.demo.permission.mapper.PermDynamicRuleMapper;
import com.example.demo.permission.service.DynamicRuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 动态规则服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicRuleServiceImpl extends ServiceImpl<PermDynamicRuleMapper, PermDynamicRule> implements DynamicRuleService {

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    @Override
    public Page<PermDynamicRule> queryPage(Map<String, Object> params) {
        int pageNum = params.get("pageNum") != null ? Integer.parseInt(params.get("pageNum").toString()) : 1;
        int pageSize = params.get("pageSize") != null ? Integer.parseInt(params.get("pageSize").toString()) : 10;

        QueryWrapper<PermDynamicRule> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText((String) params.get("ruleType"))) {
            wrapper.eq("rule_type", params.get("ruleType"));
        }
        if (StringUtils.hasText((String) params.get("ruleName"))) {
            wrapper.like("rule_name", params.get("ruleName"));
        }
        wrapper.orderByAsc("priority");

        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public PermDynamicRule createRule(PermDynamicRule rule) {
        rule.setStatus(1);
        save(rule);
        log.info("Created rule: {}", rule.getRuleCode());
        return rule;
    }

    @Override
    public PermDynamicRule updateRule(PermDynamicRule rule) {
        updateById(rule);
        log.info("Updated rule: {}", rule.getRuleCode());
        return rule;
    }

    @Override
    public void enableRule(Long ruleId) {
        PermDynamicRule rule = getById(ruleId);
        if (rule != null) {
            rule.setStatus(1);
            updateById(rule);
            log.info("Enabled rule: {}", rule.getRuleCode());
        }
    }

    @Override
    public void disableRule(Long ruleId) {
        PermDynamicRule rule = getById(ruleId);
        if (rule != null) {
            rule.setStatus(0);
            updateById(rule);
            log.info("Disabled rule: {}", rule.getRuleCode());
        }
    }

    @Override
    public DecisionResult testRule(Long ruleId, DecisionRequest request) {
        PermDynamicRule rule = getById(ruleId);
        if (rule == null) {
            return DecisionResult.deny(request.getRequestId(), "规则不存在");
        }

        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("userId", request.getUserId());
            context.setVariable("resourceType", request.getResourceType());
            context.setVariable("resourceId", request.getResourceId());
            context.setVariable("operation", request.getOperation());

            Boolean matches = expressionParser.parseExpression(rule.getConditionExpression())
                    .getValue(context, Boolean.class);

            if (Boolean.TRUE.equals(matches)) {
                return DecisionResult.builder()
                        .requestId(request.getRequestId())
                        .decision(switch (rule.getActionType()) {
                            case "ALLOW" -> com.example.demo.permission.context.DecisionType.ALLOW;
                            case "DENY" -> com.example.demo.permission.context.DecisionType.DENY;
                            default -> com.example.demo.permission.context.DecisionType.CONDITIONAL;
                        })
                        .matchedRules(List.of(rule.getRuleCode()))
                        .build();
            } else {
                return DecisionResult.allow(request.getRequestId());
            }

        } catch (Exception e) {
            log.error("Rule test failed", e);
            return DecisionResult.deny(request.getRequestId(), "规则测试失败: " + e.getMessage());
        }
    }

    @Override
    public List<PermDynamicRule> getActiveRules() {
        return baseMapper.selectEnabledRulesByPriority();
    }

    @Override
    public List<PermDynamicRule> getRulesByType(String ruleType) {
        return list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PermDynamicRule>()
                .eq("rule_type", ruleType)
                .orderByAsc("priority"));
    }
}

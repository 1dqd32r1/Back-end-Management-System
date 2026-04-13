package com.example.demo.permission.explanation.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.permission.entity.PermDecisionLog;
import com.example.demo.permission.entity.PermDynamicRule;
import com.example.demo.permission.mapper.PermDecisionLogMapper;
import com.example.demo.permission.mapper.PermDynamicRuleMapper;
import com.example.demo.permission.explanation.ExplanationGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限决策解释生成器实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExplanationGeneratorImpl implements ExplanationGenerator {

    private final PermDecisionLogMapper decisionLogMapper;
    private final PermDynamicRuleMapper ruleMapper;
    private final ObjectMapper objectMapper;

    @Override
    public DecisionExplanation generateExplanation(String requestId) {
        PermDecisionLog log = decisionLogMapper.selectOne(
                new QueryWrapper<PermDecisionLog>().eq("request_id", requestId)
        );

        if (log == null) {
            return new DecisionExplanation(
                    requestId, "UNKNOWN", "未找到决策记录",
                    Collections.emptyList(), Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList(), Collections.emptyMap()
            );
        }

        // 解析匹配的规则
        List<String> matchedRules = parseJsonList(log.getMatchedRules());
        List<String> denialReasons = new ArrayList<>();
        if (log.getDenialReason() != null) {
            denialReasons.add(log.getDenialReason());
        }

        // 生成因子解释
        List<FactorExplanation> factors = generateFactors(log);

        // 生成建议
        List<Recommendation> recommendations = generateRecommendations(log);

        // 构建上下文
        Map<String, Object> context = new HashMap<>();
        context.put("resourceType", log.getResourceType());
        context.put("resourceId", log.getResourceId());
        context.put("operation", log.getOperation());
        context.put("userId", log.getUserId());

        String summary = generateSummary(log);

        return new DecisionExplanation(
                requestId, log.getFinalDecision(), summary, factors,
                matchedRules, denialReasons, recommendations, context
        );
    }

    @Override
    public NaturalLanguageExplanation generateNaturalLanguage(String requestId) {
        PermDecisionLog log = decisionLogMapper.selectOne(
                new QueryWrapper<PermDecisionLog>().eq("request_id", requestId)
        );

        if (log == null) {
            return new NaturalLanguageExplanation(
                    requestId, "无法解释", "未找到决策记录",
                    Collections.emptyList(), "请提供有效的请求ID。", "LOW"
            );
        }

        String title = switch (log.getFinalDecision()) {
            case "ALLOW" -> "访问已授权";
            case "DENY" -> "访问被拒绝";
            case "CONDITIONAL" -> "有条件授权";
            default -> "决策结果";
        };

        List<String> bulletPoints = new ArrayList<>();
        bulletPoints.add("用户ID: " + log.getUserId());
        bulletPoints.add("资源类型: " + log.getResourceType());
        bulletPoints.add("操作: " + log.getOperation());

        List<String> matchedRules = parseJsonList(log.getMatchedRules());
        if (!matchedRules.isEmpty()) {
            bulletPoints.add("匹配规则: " + String.join(", ", matchedRules));
        }

        String summary = switch (log.getFinalDecision()) {
            case "ALLOW" -> "您的访问请求已通过权限验证。";
            case "DENY" -> "您的访问请求被拒绝，原因: " + (log.getDenialReason() != null ? log.getDenialReason() : "权限不足");
            case "CONDITIONAL" -> "您的访问请求需要满足额外条件才能执行。";
            default -> "无法确定决策结果。";
        };

        String detailedExplanation = buildDetailedExplanation(log);

        String confidence = matchedRules.isEmpty() ? "LOW" : matchedRules.size() > 2 ? "HIGH" : "MEDIUM";

        return new NaturalLanguageExplanation(
                requestId, title, summary, bulletPoints, detailedExplanation, confidence
        );
    }

    @Override
    public DecisionPath generateDecisionPath(String requestId) {
        PermDecisionLog log = decisionLogMapper.selectOne(
                new QueryWrapper<PermDecisionLog>().eq("request_id", requestId)
        );

        if (log == null) {
            return new DecisionPath(requestId, Collections.emptyList(), "UNKNOWN", "未找到决策记录");
        }

        List<String> matchedRuleNames = parseJsonList(log.getMatchedRules());
        List<DecisionStep> steps = new ArrayList<>();
        int stepNum = 1;

        // 基础权限检查步骤
        steps.add(new DecisionStep(
                stepNum++, "基础权限检查", "用户是否拥有该资源的基本访问权限",
                true, "PASS", "用户角色包含基本访问权限"
        ));

        // 动态规则评估步骤
        for (String ruleName : matchedRuleNames) {
            PermDynamicRule rule = ruleMapper.selectOne(
                    new QueryWrapper<PermDynamicRule>().eq("rule_name", ruleName)
            );

            if (rule != null) {
                steps.add(new DecisionStep(
                        stepNum++, rule.getRuleName(), rule.getConditionExpression(),
                        true, rule.getActionType(),
                        "规则【" + rule.getRuleName() + "】条件满足，执行" + rule.getActionType() + "动作"
                ));
            }
        }

        // 最终决策步骤
        steps.add(new DecisionStep(
                stepNum, "最终决策", "综合所有规则结果",
                true, log.getFinalDecision(),
                "基于" + matchedRuleNames.size() + "条规则的评估结果，最终决策为" + log.getFinalDecision()
        ));

        String reasoning = buildReasoning(log, matchedRuleNames);

        return new DecisionPath(requestId, steps, log.getFinalDecision(), reasoning);
    }

    @Override
    public List<RuleExplanation> explainRules(List<Long> ruleIds) {
        if (ruleIds == null || ruleIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<PermDynamicRule> rules = ruleMapper.selectBatchIds(ruleIds);
        return rules.stream().map(rule -> new RuleExplanation(
                rule.getRuleId(),
                rule.getRuleName(),
                rule.getRuleCode(),
                rule.getConditionExpression(),
                rule.getActionType(),
                generateBusinessMeaning(rule),
                generateExamples(rule)
        )).collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    private List<FactorExplanation> generateFactors(PermDecisionLog log) {
        List<FactorExplanation> factors = new ArrayList<>();

        // 资源类型因子
        factors.add(new FactorExplanation(
                "resourceType", log.getResourceType(), 0.2,
                "POSITIVE", "资源类型为" + log.getResourceType()
        ));

        // 操作类型因子
        factors.add(new FactorExplanation(
                "operation", log.getOperation(), 0.25,
                "NEUTRAL", "操作类型为" + log.getOperation()
        ));

        // 规则匹配因子
        List<String> rules = parseJsonList(log.getMatchedRules());
        factors.add(new FactorExplanation(
                "matchedRules", String.valueOf(rules.size()), 0.35,
                rules.isEmpty() ? "NEGATIVE" : "POSITIVE",
                "匹配了" + rules.size() + "条规则"
        ));

        // 决策结果因子
        factors.add(new FactorExplanation(
                "decision", log.getFinalDecision(), 0.2,
                log.getFinalDecision().equals("ALLOW") ? "POSITIVE" : "NEGATIVE",
                "最终决策: " + log.getFinalDecision()
        ));

        return factors;
    }

    private List<Recommendation> generateRecommendations(PermDecisionLog log) {
        List<Recommendation> recommendations = new ArrayList<>();

        if ("DENY".equals(log.getFinalDecision())) {
            recommendations.add(new Recommendation(
                    "ACCESS", "申请权限",
                    "如果您需要访问该资源，请联系管理员申请相应权限",
                    1
            ));

            if (log.getDenialReason() != null && log.getDenialReason().contains("时间")) {
                recommendations.add(new Recommendation(
                        "TIMING", "调整访问时间",
                        "该资源在特定时间段可访问，请调整您的访问时间",
                        2
                ));
            }
        }

        return recommendations;
    }

    private String generateSummary(PermDecisionLog log) {
        List<String> rules = parseJsonList(log.getMatchedRules());

        return switch (log.getFinalDecision()) {
            case "ALLOW" -> String.format("用户%d对%s资源的%s操作已授权，匹配%d条规则",
                    log.getUserId(), log.getResourceType(), log.getOperation(), rules.size());
            case "DENY" -> String.format("用户%d对%s资源的%s操作被拒绝，原因: %s",
                    log.getUserId(), log.getResourceType(), log.getOperation(),
                    log.getDenialReason() != null ? log.getDenialReason() : "权限不足");
            case "CONDITIONAL" -> String.format("用户%d对%s资源的%s操作需要满足额外条件",
                    log.getUserId(), log.getResourceType(), log.getOperation());
            default -> "决策结果未知";
        };
    }

    private String buildDetailedExplanation(PermDecisionLog log) {
        StringBuilder sb = new StringBuilder();
        sb.append("本次权限决策基于多维度分析。");

        List<String> rules = parseJsonList(log.getMatchedRules());
        if (!rules.isEmpty()) {
            sb.append("系统评估了以下规则: ").append(String.join("、", rules)).append("。");
        }

        if ("DENY".equals(log.getFinalDecision()) && log.getDenialReason() != null) {
            sb.append("拒绝原因: ").append(log.getDenialReason()).append("。");
        }

        if (log.getExplanation() != null) {
            sb.append(log.getExplanation());
        }

        return sb.toString();
    }

    private String buildReasoning(PermDecisionLog log, List<String> matchedRules) {
        StringBuilder sb = new StringBuilder();
        sb.append("决策推理过程: ");

        if (matchedRules.isEmpty()) {
            sb.append("未匹配到任何动态规则，");
        } else {
            sb.append("匹配到").append(matchedRules.size()).append("条规则，");
        }

        sb.append("基于用户角色权限和动态规则评估，最终决策为").append(log.getFinalDecision());

        return sb.toString();
    }

    private String generateBusinessMeaning(PermDynamicRule rule) {
        String typeDesc = switch (rule.getRuleType()) {
            case "TIME" -> "时间访问控制规则";
            case "GEO" -> "地理位置访问控制规则";
            case "BEHAVIOR" -> "行为模式访问控制规则";
            case "RISK" -> "风险评估规则";
            case "CUSTOM" -> "自定义业务规则";
            default -> "业务规则";
        };

        String actionDesc = switch (rule.getActionType()) {
            case "ALLOW" -> "允许访问";
            case "DENY" -> "拒绝访问";
            case "ELEVATE" -> "提升权限";
            case "DEGRADE" -> "降低权限";
            default -> "执行操作";
        };

        return typeDesc + "，当条件满足时" + actionDesc;
    }

    private List<String> generateExamples(PermDynamicRule rule) {
        List<String> examples = new ArrayList<>();

        if (rule.getRuleType().equals("TIME")) {
            examples.add("示例: 在工作时间内(9:00-18:00)允许访问敏感数据");
            examples.add("示例: 禁止在周末访问生产环境");
        } else if (rule.getRuleType().equals("GEO")) {
            examples.add("示例: 仅允许从办公网络IP段访问财务系统");
            examples.add("示例: 禁止从境外IP访问核心业务系统");
        } else if (rule.getRuleType().equals("BEHAVIOR")) {
            examples.add("示例: 连续访问失败5次后锁定账户");
            examples.add("示例: 检测到异常访问模式时触发二次认证");
        } else if (rule.getRuleType().equals("RISK")) {
            examples.add("示例: 风险评分超过阈值时拒绝操作");
            examples.add("示例: 高风险操作需要审批");
        }

        return examples;
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

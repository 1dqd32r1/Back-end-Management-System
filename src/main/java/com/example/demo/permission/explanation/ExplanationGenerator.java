package com.example.demo.permission.explanation;

import java.util.List;
import java.util.Map;

/**
 * 权限决策解释生成器接口
 */
public interface ExplanationGenerator {

    /**
     * 生成决策解释
     * @param requestId 请求ID
     * @return 解释结果
     */
    DecisionExplanation generateExplanation(String requestId);

    /**
     * 生成自然语言解释
     * @param requestId 请求ID
     * @return 自然语言解释
     */
    NaturalLanguageExplanation generateNaturalLanguage(String requestId);

    /**
     * 生成决策路径图
     * @param requestId 请求ID
     * @return 决策路径
     */
    DecisionPath generateDecisionPath(String requestId);

    /**
     * 获取相关规则说明
     * @param ruleIds 规则ID列表
     * @return 规则说明列表
     */
    List<RuleExplanation> explainRules(List<Long> ruleIds);

    // ==================== 结果类 ====================

    record DecisionExplanation(
            String requestId,
            String decision,
            String summary,
            List<FactorExplanation> factors,
            List<String> matchedRules,
            List<String> denialReasons,
            List<Recommendation> recommendations,
            Map<String, Object> context
    ) {}

    record FactorExplanation(
            String factorName,
            String factorValue,
            double weight,
            String impact,  // POSITIVE, NEGATIVE, NEUTRAL
            String description
    ) {}

    record NaturalLanguageExplanation(
            String requestId,
            String title,
            String summary,
            List<String> bulletPoints,
            String detailedExplanation,
            String confidence
    ) {}

    record DecisionPath(
            String requestId,
            List<DecisionStep> steps,
            String finalDecision,
            String reasoning
    ) {}

    record DecisionStep(
            int stepNumber,
            String ruleName,
            String condition,
            boolean matched,
            String outcome,
            String explanation
    ) {}

    record RuleExplanation(
            Long ruleId,
            String ruleName,
            String ruleCode,
            String condition,
            String action,
            String businessMeaning,
            List<String> examples
    ) {}

    record Recommendation(
            String type,
            String action,
            String description,
            int priority
    ) {}
}

package com.example.demo.permission.controller;

import com.example.demo.common.Result;
import com.example.demo.permission.explanation.ExplanationGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 权限决策解释控制器
 */
@RestController
@RequestMapping("/api/permission/explanation")
@RequiredArgsConstructor
public class ExplanationController {

    private final ExplanationGenerator explanationGenerator;

    /**
     * 获取决策解释
     */
    @GetMapping("/{requestId}")
    public Result<ExplanationGenerator.DecisionExplanation> getExplanation(@PathVariable String requestId) {
        ExplanationGenerator.DecisionExplanation explanation = explanationGenerator.generateExplanation(requestId);
        return Result.success(explanation);
    }

    /**
     * 获取自然语言解释
     */
    @GetMapping("/natural/{requestId}")
    public Result<ExplanationGenerator.NaturalLanguageExplanation> getNaturalLanguage(@PathVariable String requestId) {
        ExplanationGenerator.NaturalLanguageExplanation explanation = explanationGenerator.generateNaturalLanguage(requestId);
        return Result.success(explanation);
    }

    /**
     * 获取决策路径
     */
    @GetMapping("/path/{requestId}")
    public Result<ExplanationGenerator.DecisionPath> getDecisionPath(@PathVariable String requestId) {
        ExplanationGenerator.DecisionPath path = explanationGenerator.generateDecisionPath(requestId);
        return Result.success(path);
    }

    /**
     * 解释规则
     */
    @PostMapping("/rules")
    public Result<List<ExplanationGenerator.RuleExplanation>> explainRules(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Long> ruleIds = ((List<?>) params.get("ruleIds")).stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .toList();

        List<ExplanationGenerator.RuleExplanation> explanations = explanationGenerator.explainRules(ruleIds);
        return Result.success(explanations);
    }
}

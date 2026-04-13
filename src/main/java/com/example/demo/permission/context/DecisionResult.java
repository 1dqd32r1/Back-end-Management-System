package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 权限决策结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionResult {

    /** 请求ID */
    private String requestId;

    /** 决策类型 */
    private DecisionType decision;

    /** 匹配的规则列表 */
    private List<String> matchedRules;

    /** 拒绝原因 */
    private String denialReason;

    /** 可解释性说明 */
    private String explanation;

    /** 决策延迟（毫秒） */
    private Long latencyMs;

    /** 元数据 */
    private Map<String, Object> metadata;

    /** 维度因子列表 */
    private List<DimensionFactor> dimensionFactors;

    /** 反事实解释（如果是拒绝） */
    private CounterfactualExplanation counterfactual;

    /**
     * 创建允许结果
     */
    public static DecisionResult allow(String requestId) {
        return DecisionResult.builder()
                .requestId(requestId)
                .decision(DecisionType.ALLOW)
                .build();
    }

    /**
     * 创建拒绝结果
     */
    public static DecisionResult deny(String requestId, String reason) {
        return DecisionResult.builder()
                .requestId(requestId)
                .decision(DecisionType.DENY)
                .denialReason(reason)
                .build();
    }

    /**
     * 创建条件允许结果
     */
    public static DecisionResult conditional(String requestId, String explanation) {
        return DecisionResult.builder()
                .requestId(requestId)
                .decision(DecisionType.CONDITIONAL)
                .explanation(explanation)
                .build();
    }

    /**
     * 是否允许
     */
    public boolean isAllowed() {
        return decision == DecisionType.ALLOW;
    }

    /**
     * 是否拒绝
     */
    public boolean isDenied() {
        return decision == DecisionType.DENY;
    }
}

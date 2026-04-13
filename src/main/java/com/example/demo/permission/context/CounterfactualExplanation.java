package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 反事实解释
 * 用于回答"为什么拒绝我"以及"如何才能获得权限"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounterfactualExplanation {

    /** 原始决策 */
    private String originalDecision;

    /** 必要的条件变更列表 */
    private List<ConditionChange> necessaryChanges;

    /** 建议的操作 */
    private String suggestedAction;

    /** If-Then 语句（如："如果您被授予项目经理角色，则可以编辑此项目"） */
    private String ifThenStatement;

    /**
     * 条件变更
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionChange {

        /** 条件名称 */
        private String condition;

        /** 当前值 */
        private String currentValue;

        /** 所需值 */
        private String requiredValue;

        /** 如何达成（操作建议） */
        private String howToAchieve;
    }
}

package com.example.demo.permission.context;

/**
 * 决策类型枚举
 */
public enum DecisionType {
    /**
     * 允许
     */
    ALLOW,

    /**
     * 拒绝
     */
    DENY,

    /**
     * 条件允许（需要额外验证）
     */
    CONDITIONAL
}

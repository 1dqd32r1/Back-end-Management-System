package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动态权限规则实体类
 */
@Data
@TableName("perm_dynamic_rule")
public class PermDynamicRule {

    @TableId(type = IdType.AUTO)
    private Long ruleId;

    /** 规则名称 */
    private String ruleName;

    /** 规则编码 */
    private String ruleCode;

    /** 规则类型(TIME/GEO/BEHAVIOR/RISK/CUSTOM) */
    private String ruleType;

    /** 作用维度 */
    private String dimension;

    /** 条件表达式(SpEL) */
    private String conditionExpression;

    /** 动作类型(ALLOW/DENY/ELEVATE/DEGRADE) */
    private String actionType;

    /** 优先级(越小越高) */
    private Integer priority;

    /** 生效开始时间 */
    private LocalDateTime effectiveStart;

    /** 生效结束时间 */
    private LocalDateTime effectiveEnd;

    /** 状态(0停用 1启用) */
    private Integer status;

    /** 规则描述 */
    private String description;

    /** 创建人 */
    private Long createdBy;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}

package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 风险维度上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskContext {

    /** 操作敏感度（0-10） */
    private Integer operationSensitivity;

    /** 数据分级（0-4: PUBLIC/INTERNAL/CONFIDENTIAL/SECRET/TOP_SECRET） */
    private Integer dataClassification;

    /** 数据分级编码 */
    private String dataClassCode;

    /** 异常评分（0-100） */
    private BigDecimal anomalyScore;

    /** 风险等级（0-4） */
    private Integer riskLevel;

    /** 风险因素列表 */
    private String riskFactors;

    /** 是否高风险操作 */
    private Boolean isHighRisk;

    /** 是否需要审批 */
    private Boolean requiresApproval;

    /** 已审批人数 */
    private Integer approvedCount;

    /** 需要审批人数 */
    private Integer requiredApprovals;

    /** 最近异常事件数量（24小时内） */
    private Integer recentAnomalyCount;

    /** 用户信任评分（0-100） */
    private BigDecimal trustScore;
}

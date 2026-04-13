package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 权限上下文快照实体类
 */
@Data
@TableName("perm_context_snapshot")
public class PermContextSnapshot {

    @TableId(type = IdType.AUTO)
    private Long snapshotId;

    /** 用户ID */
    private Long userId;

    /** 会话ID */
    private String sessionId;

    // ==================== 组织维度 ====================

    /** 当前部门ID */
    private Long deptId;

    /** 岗位ID列表(JSON) */
    private String postIds;

    /** 虚拟团队ID列表(JSON) */
    private String virtualTeamIds;

    // ==================== 实体维度 ====================

    /** 当前操作实体类型 */
    private String activeEntityType;

    /** 当前操作实体ID */
    private Long activeEntityId;

    // ==================== 时空维度 ====================

    /** IP地址 */
    private String ipAddress;

    /** 地理位置(经纬度) */
    private String geoLocation;

    /** 设备指纹 */
    private String deviceFingerprint;

    /** 访问时间段 */
    private String accessTimeRange;

    /** 访问频率(次/小时) */
    private Integer accessFrequency;

    // ==================== 风险维度 ====================

    /** 操作敏感度(0-10) */
    private Integer operationSensitivity;

    /** 数据分级(0-4) */
    private Integer dataClassification;

    /** 异常评分(0-100) */
    private BigDecimal anomalyScore;

    /** 风险等级(0-4) */
    private Integer riskLevel;

    // ==================== 决策结果 ====================

    /** 决策结果(ALLOW/DENY/CONDITIONAL) */
    private String decisionResult;

    /** 决策延迟(毫秒) */
    private Integer decisionLatencyMs;

    /** 决策因子(JSON) */
    private String decisionFactors;

    // ==================== 元数据 ====================

    /** 请求追踪ID */
    private String requestId;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

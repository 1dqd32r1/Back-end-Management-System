package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 权限上下文快照
 * 包含五维权限空间的所有数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextSnapshot {

    /** 快照ID */
    private String snapshotId;

    /** 用户ID */
    private Long userId;

    /** 会话ID */
    private String sessionId;

    /** 快照时间 */
    private LocalDateTime timestamp;

    /** 请求ID */
    private String requestId;

    // ==================== 五维数据 ====================

    /** 组织维度 */
    private OrganizationalContext organizational;

    /** 实体维度 */
    private EntityContext entity;

    /** 时空维度 */
    private SpatiotemporalContext spatiotemporal;

    /** 风险维度 */
    private RiskContext risk;

    /** 行为维度 */
    private BehavioralContext behavioral;

    // ==================== 原始数据 ====================

    /** 原始上下文数据 */
    private Map<String, Object> rawContext;

    /**
     * 创建空快照
     */
    public static ContextSnapshot empty() {
        return ContextSnapshot.builder()
                .snapshotId(UUID.randomUUID().toString())
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 创建基础快照（仅用户ID）
     */
    public static ContextSnapshot basic(Long userId) {
        return ContextSnapshot.builder()
                .snapshotId(UUID.randomUUID().toString())
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 转换为Map格式（用于存储）
     */
    public Map<String, Object> toMap() {
        return Map.of(
                "snapshotId", snapshotId != null ? snapshotId : "",
                "userId", userId != null ? userId : 0,
                "sessionId", sessionId != null ? sessionId : "",
                "timestamp", timestamp != null ? timestamp.toString() : "",
                "organizational", organizational != null ? organizational.toString() : "",
                "entity", entity != null ? entity.toString() : "",
                "spatiotemporal", spatiotemporal != null ? spatiotemporal.toString() : "",
                "risk", risk != null ? risk.toString() : "",
                "behavioral", behavioral != null ? behavioral.toString() : ""
        );
    }
}

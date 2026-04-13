package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限决策日志实体类
 */
@Data
@TableName("perm_decision_log")
public class PermDecisionLog {

    @TableId(type = IdType.AUTO)
    private Long logId;

    /** 请求追踪ID */
    private String requestId;

    /** 用户ID */
    private Long userId;

    /** 资源类型 */
    private String resourceType;

    /** 资源ID */
    private String resourceId;

    /** 操作类型 */
    private String operation;

    /** 上下文快照ID */
    private Long snapshotId;

    /** 匹配的规则列表(JSON) */
    private String matchedRules;

    /** 冲突消解记录(JSON) */
    private String conflictResolution;

    /** 最终决策 */
    private String finalDecision;

    /** 拒绝原因 */
    private String denialReason;

    /** 可解释性说明 */
    private String explanation;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

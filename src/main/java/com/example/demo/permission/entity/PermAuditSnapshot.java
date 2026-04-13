package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限审计快照实体类(时光机)
 */
@Data
@TableName("perm_audit_snapshot")
public class PermAuditSnapshot {

    @TableId(type = IdType.AUTO)
    private Long auditId;

    /** 用户ID */
    private Long userId;

    /** 快照时间点 */
    private LocalDateTime snapshotTime;

    // ==================== 权限状态快照 ====================

    /** 角色列表快照(JSON) */
    private String roles;

    /** 权限列表快照(JSON) */
    private String permissions;

    /** 实体权限快照(JSON) */
    private String entityPermissions;

    /** 数据范围快照 */
    private String dataScope;

    // ==================== 关联变更 ====================

    /** 触发变更的事件ID */
    private Long changeEventId;

    /** 变更类型 */
    private String changeType;

    /** 变更原因 */
    private String changeReason;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

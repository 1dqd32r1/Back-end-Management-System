package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限变更事件实体类(CRDT同步)
 */
@Data
@TableName("perm_change_event")
public class PermChangeEvent {

    @TableId(type = IdType.AUTO)
    private Long eventId;

    /** 事件类型 */
    private String eventType;

    /** 事件来源服务 */
    private String eventSource;

    // ==================== 变更内容 ====================

    /** 目标用户ID */
    private Long targetUserId;

    /** 目标角色ID */
    private Long targetRoleId;

    /** 变更数据(JSON) */
    private String changeData;

    // ==================== CRDT元数据 ====================

    /** 向量时钟 */
    private String vectorClock;

    /** 操作ID(幂等) */
    private String operationId;

    /** 墓碑标记 */
    private Integer tombstone;

    // ==================== 同步状态 ====================

    /** 已同步服务列表(JSON) */
    private String syncedServices;

    /** 同步状态 */
    private String syncStatus;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

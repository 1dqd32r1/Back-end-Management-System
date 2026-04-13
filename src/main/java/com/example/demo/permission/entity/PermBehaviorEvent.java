package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 行为事件流实体类
 */
@Data
@TableName("perm_behavior_event")
public class PermBehaviorEvent {

    @TableId(type = IdType.AUTO)
    private Long eventId;

    /** 用户ID */
    private Long userId;

    /** 事件类型 */
    private String eventType;

    /** 事件来源(WEB/API/SDK) */
    private String eventSource;

    // ==================== 事件详情 ====================

    /** 资源类型 */
    private String resourceType;

    /** 资源ID */
    private String resourceId;

    /** 操作类型 */
    private String operation;

    /** 事件详细数据(JSON) */
    private String eventData;

    // ==================== 上下文 ====================

    /** 会话ID */
    private String sessionId;

    /** IP地址 */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 地理位置 */
    private String geoLocation;

    // ==================== 分析标记 ====================

    /** 是否异常 */
    private Integer isAnomaly;

    /** 异常类型 */
    private String anomalyType;

    /** 是否已处理 */
    private Integer processed;

    /** 事件时间 */
    private LocalDateTime eventTime;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

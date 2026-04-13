package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 联邦服务注册实体类
 */
@Data
@TableName("perm_federation_service")
public class PermFederationService {

    @TableId(type = IdType.AUTO)
    private Long serviceId;

    /** 服务名称 */
    private String serviceName;

    /** 服务编码 */
    private String serviceCode;

    /** 服务地址 */
    private String serviceUrl;

    // ==================== 同步配置 ====================

    /** 同步模式(EVENTUAL/STRONG) */
    private String syncMode;

    /** 同步间隔(毫秒) */
    private Integer syncInterval;

    /** 最后同步时间 */
    private LocalDateTime lastSyncTime;

    /** 同步状态 */
    private String syncStatus;

    // ==================== CRDT状态 ====================

    /** 向量时钟 */
    private String crdtClock;

    /** 状态哈希 */
    private String crdtStateHash;

    /** 服务状态(0离线 1在线) */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}

package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 权限熵值监控实体类
 */
@Data
@TableName("perm_entropy_monitor")
public class PermEntropyMonitor {

    @TableId(type = IdType.AUTO)
    private Long monitorId;

    /** 用户ID */
    private Long userId;

    // ==================== 熵值计算 ====================

    /** 权限熵值 */
    private BigDecimal permissionEntropy;

    /** 有效权限数 */
    private Integer effectivePermissions;

    /** 已使用权限数 */
    private Integer usedPermissions;

    /** 未使用权限数 */
    private Integer unusedPermissions;

    // ==================== 权限漂移检测 ====================

    /** 权限漂移评分 */
    private BigDecimal driftScore;

    /** 权限膨胀指标(JSON) */
    private String creepIndicators;

    /** 建议回收的权限(JSON) */
    private String recommendedRevocations;

    // ==================== 统计周期 ====================

    /** 统计周期开始 */
    private LocalDateTime periodStart;

    /** 统计周期结束 */
    private LocalDateTime periodEnd;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

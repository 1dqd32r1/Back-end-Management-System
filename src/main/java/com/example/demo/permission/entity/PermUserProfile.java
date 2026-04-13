package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户行为画像实体类
 */
@Data
@TableName("perm_user_profile")
public class PermUserProfile {

    @TableId(type = IdType.AUTO)
    private Long profileId;

    /** 用户ID */
    private Long userId;

    // ==================== 行为模式 ====================

    /** 典型访问时段(JSON数组) */
    private String typicalAccessHours;

    /** 典型位置列表(JSON) */
    private String typicalLocations;

    /** 典型设备列表(JSON) */
    private String typicalDevices;

    /** 频繁操作统计(JSON) */
    private String frequentOperations;

    /** 协作模式图谱(JSON) */
    private String collaborationPatterns;

    // ==================== 技能图谱 ====================

    /** 技能标签(JSON) */
    private String skillTags;

    /** 专业领域(JSON) */
    private String expertiseDomains;

    // ==================== 权限使用统计 ====================

    /** 权限使用统计(JSON) */
    private String permissionUsageStats;

    /** 未使用权限(JSON) */
    private String unusedPermissions;

    /** 过度授权评分 */
    private BigDecimal overprivilegedScore;

    // ==================== 行为基线 ====================

    /** 行为基线模型(JSON) */
    private String behaviorBaseline;

    /** 基线最后更新时间 */
    private LocalDateTime lastBaselineUpdate;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}

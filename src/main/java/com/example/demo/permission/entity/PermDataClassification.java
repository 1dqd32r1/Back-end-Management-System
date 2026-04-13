package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据分级配置实体类
 */
@Data
@TableName("perm_data_classification")
public class PermDataClassification {

    @TableId(type = IdType.AUTO)
    private Long classId;

    /** 分级名称 */
    private String className;

    /** 分级级别(0-4) */
    private Integer classLevel;

    /** 分级编码(PUBLIC/INTERNAL/CONFIDENTIAL/SECRET/TOP_SECRET) */
    private String classCode;

    // ==================== 访问规则 ====================

    /** 最低角色级别要求 */
    private Integer minRoleLevel;

    /** 审批人数要求 */
    private Integer requiredApprovals;

    /** 访问时间限制 */
    private String accessTimeRestriction;

    /** 分级描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createdAt;
}

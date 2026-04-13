package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 虚拟团队实体类
 */
@Data
@TableName("sys_virtual_team")
public class SysVirtualTeam {

    @TableId(type = IdType.AUTO)
    private Long teamId;

    /** 团队名称 */
    private String teamName;

    /** 团队编码 */
    private String teamCode;

    /** 团队类型(PROJECT/CROSS_DEPT/TASK) */
    private String teamType;

    // ==================== 关联信息 ====================

    /** 关联项目ID */
    private Long projectId;

    /** 归属部门ID */
    private Long parentDeptId;

    // ==================== 权限配置 ====================

    /** 继承的权限(JSON) */
    private String inheritedPermissions;

    /** 独有权限(JSON) */
    private String exclusivePermissions;

    // ==================== 成员管理 ====================

    /** 负责人ID */
    private Long leaderId;

    /** 成员数量 */
    private Integer memberCount;

    // ==================== 生效配置 ====================

    /** 生效开始时间 */
    private LocalDateTime effectiveStart;

    /** 生效结束时间 */
    private LocalDateTime effectiveEnd;

    /** 状态 */
    private Integer status;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}

package com.example.demo.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限沙盒模拟记录实体类
 */
@Data
@TableName("perm_sandbox_simulation")
public class PermSandboxSimulation {

    @TableId(type = IdType.AUTO)
    private Long simulationId;

    /** 模拟名称 */
    private String simulationName;

    /** 模拟类型(WHAT_IF/TIME_TRAVEL/STRESS) */
    private String simulationType;

    // ==================== 模拟配置 ====================

    /** 基础快照ID */
    private Long baseSnapshotId;

    /** 目标时间点(时光机) */
    private LocalDateTime targetTimestamp;

    /** 变量覆盖配置(JSON) */
    private String variableOverrides;

    // ==================== 模拟结果 ====================

    /** 受影响用户列表(JSON) */
    private String affectedUsers;

    /** 影响面热力图数据(JSON) */
    private String impactHeatmap;

    /** 冲突预测(JSON) */
    private String conflictPredictions;

    // ==================== 执行信息 ====================

    /** 执行人 */
    private Long executedBy;

    /** 执行时间 */
    private LocalDateTime executionTime;

    /** 执行耗时(毫秒) */
    private Integer executionDurationMs;
}

package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 行为维度上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BehavioralContext {

    /** 用户习惯访问时段 */
    private String typicalAccessHours;

    /** 当前访问是否符合习惯 */
    private Boolean isTypicalAccessTime;

    /** 常用位置列表 */
    private List<String> typicalLocations;

    /** 当前位置是否常用位置 */
    private Boolean isTypicalLocation;

    /** 常用设备列表 */
    private List<String> typicalDevices;

    /** 当前设备是否常用设备 */
    private Boolean isTypicalDevice;

    /** 频繁操作列表 */
    private List<String> frequentOperations;

    /** 当前操作是否频繁操作 */
    private Boolean isFrequentOperation;

    /** 协作模式（用户常协作的其他用户ID列表） */
    private List<Long> collaborators;

    /** 技能标签 */
    private List<String> skillTags;

    /** 权限使用频率（最近30天） */
    private BigDecimal permissionUsageRate;

    /** 行为异常评分（0-100） */
    private BigDecimal behaviorAnomalyScore;

    /** 预测下一步操作 */
    private String predictedNextOperation;

    /** 预测置信度 */
    private BigDecimal predictionConfidence;
}

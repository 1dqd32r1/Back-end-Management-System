package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 维度因子
 * 表示单个维度对权限决策的影响
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DimensionFactor {

    /** 维度名称 */
    private String dimension;

    /** 评分（0-1） */
    private Double score;

    /** 权重 */
    private Double weight;

    /** 贡献值 = score * weight */
    private Double contribution;

    /** 影响因素说明 */
    private List<String> reasons;

    /** 详细数据 */
    private Map<String, Object> details;

    /**
     * 计算贡献值
     */
    public void calculateContribution() {
        if (score != null && weight != null) {
            this.contribution = score * weight;
        }
    }

    /**
     * 创建默认维度因子
     */
    public static DimensionFactor defaultFactor(String dimension) {
        return DimensionFactor.builder()
                .dimension(dimension)
                .score(1.0)
                .weight(1.0)
                .contribution(1.0)
                .build();
    }
}

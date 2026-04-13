package com.example.demo.permission.dimension.risk;

import com.example.demo.permission.context.ContextSnapshot;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DimensionFactor;
import com.example.demo.permission.context.RiskContext;
import com.example.demo.permission.dimension.DimensionHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 风险维度处理器
 */
@Component
public class RiskDimensionHandler implements DimensionHandler {

    @Override
    public String getDimensionName() {
        return "RISK";
    }

    @Override
    public double getWeight() {
        return 2.0; // 风险维度权重最高
    }

    @Override
    public int getPriority() {
        return 5; // 最高优先级
    }

    @Override
    public DimensionFactor calculate(ContextSnapshot snapshot, DecisionRequest request) {
        RiskContext risk = snapshot.getRisk();
        if (risk == null) {
            return DimensionFactor.defaultFactor(getDimensionName());
        }

        List<String> reasons = new ArrayList<>();
        Map<String, Object> details = new HashMap<>();
        double score = 1.0;

        // 检查异常评分
        if (risk.getAnomalyScore() != null) {
            double anomalyScore = risk.getAnomalyScore().doubleValue();
            details.put("anomalyScore", anomalyScore);
            if (anomalyScore > 70) {
                score -= 0.5;
                reasons.add("异常评分过高: " + anomalyScore);
            } else if (anomalyScore > 50) {
                score -= 0.2;
                reasons.add("异常评分偏高: " + anomalyScore);
            }
        }

        // 检查风险等级
        if (risk.getRiskLevel() != null) {
            details.put("riskLevel", risk.getRiskLevel());
            if (risk.getRiskLevel() >= 3) {
                score -= 0.3;
                reasons.add("高风险等级: " + risk.getRiskLevel());
            }
        }

        // 检查是否高风险操作
        if (Boolean.TRUE.equals(risk.getIsHighRisk())) {
            score -= 0.2;
            reasons.add("高风险操作");
        }

        // 检查最近异常事件
        if (risk.getRecentAnomalyCount() != null && risk.getRecentAnomalyCount() > 3) {
            score -= 0.1;
            reasons.add("近期有多次异常行为");
        }

        // 检查信任评分
        if (risk.getTrustScore() != null) {
            BigDecimal trustScore = risk.getTrustScore();
            details.put("trustScore", trustScore);
            if (trustScore.compareTo(new BigDecimal("50")) < 0) {
                score -= 0.3;
                reasons.add("信任评分过低: " + trustScore);
            }
        }

        // 归一化评分
        score = Math.max(0, Math.min(score, 1.0));

        DimensionFactor factor = DimensionFactor.builder()
                .dimension(getDimensionName())
                .score(score)
                .weight(getWeight())
                .reasons(reasons)
                .details(details)
                .build();
        factor.calculateContribution();

        return factor;
    }
}

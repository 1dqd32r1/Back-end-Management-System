package com.example.demo.permission.dimension.spatiotemporal;

import com.example.demo.permission.context.ContextSnapshot;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DimensionFactor;
import com.example.demo.permission.context.SpatiotemporalContext;
import com.example.demo.permission.dimension.DimensionHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 时空维度处理器
 */
@Component
public class SpatiotemporalDimensionHandler implements DimensionHandler {

    @Override
    public String getDimensionName() {
        return "SPATIOTEMPORAL";
    }

    @Override
    public double getWeight() {
        return 1.0;
    }

    @Override
    public int getPriority() {
        return 20;
    }

    @Override
    public DimensionFactor calculate(ContextSnapshot snapshot, DecisionRequest request) {
        SpatiotemporalContext time = snapshot.getSpatiotemporal();
        if (time == null) {
            return DimensionFactor.defaultFactor(getDimensionName());
        }

        List<String> reasons = new ArrayList<>();
        Map<String, Object> details = new HashMap<>();
        double score = 1.0;

        // 检查工作时间
        if (Boolean.TRUE.equals(time.getIsWorkingHours())) {
            reasons.add("当前在工作时间内");
            details.put("isWorkingHours", true);
        } else {
            score -= 0.2;
            reasons.add("当前不在工作时间内");
            details.put("isWorkingHours", false);
        }

        // 检查访问频率
        if (time.getAccessFrequency() != null && time.getAccessFrequency() > 100) {
            score -= 0.1;
            reasons.add("访问频率异常");
        }

        // 检查设备类型
        details.put("deviceType", time.getDeviceType());
        details.put("ipAddress", time.getIpAddress());

        // 检查是否首次访问
        if (Boolean.TRUE.equals(time.getIsFirstAccess())) {
            score -= 0.1;
            reasons.add("首次访问");
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

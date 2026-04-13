package com.example.demo.permission.dimension.organizational;

import com.example.demo.permission.context.ContextSnapshot;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DimensionFactor;
import com.example.demo.permission.context.OrganizationalContext;
import com.example.demo.permission.dimension.DimensionHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织维度处理器
 */
@Component
public class OrganizationalDimensionHandler implements DimensionHandler {

    @Override
    public String getDimensionName() {
        return "ORGANIZATIONAL";
    }

    @Override
    public double getWeight() {
        return 1.5; // 组织维度权重较高
    }

    @Override
    public int getPriority() {
        return 10; // 高优先级
    }

    @Override
    public DimensionFactor calculate(ContextSnapshot snapshot, DecisionRequest request) {
        OrganizationalContext org = snapshot.getOrganizational();
        if (org == null) {
            return DimensionFactor.defaultFactor(getDimensionName());
        }

        List<String> reasons = new ArrayList<>();
        Map<String, Object> details = new HashMap<>();
        double score = 1.0;

        // 检查部门权限
        if (org.getDeptId() != null) {
            details.put("deptId", org.getDeptId());
            details.put("deptName", org.getDeptName());
        }

        // 检查岗位权限
        if (org.getPostIds() != null && !org.getPostIds().isEmpty()) {
            details.put("postCount", org.getPostIds().size());
            reasons.add("用户拥有" + org.getPostIds().size() + "个岗位");
        }

        // 检查是否部门负责人
        if (Boolean.TRUE.equals(org.getIsDeptLeader())) {
            score += 0.2;
            reasons.add("用户是部门负责人");
        }

        // 检查数据范围
        details.put("dataScope", org.getDataScope());
        if ("1".equals(org.getDataScope())) {
            score += 0.3;
            reasons.add("用户拥有全部数据权限");
        }

        // 归一化评分
        score = Math.min(score, 1.0);

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

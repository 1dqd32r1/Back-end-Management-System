package com.example.demo.permission.dimension;

import com.example.demo.permission.context.ContextSnapshot;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DimensionFactor;

/**
 * 维度处理器接口
 * 处理五维权限空间中的单个维度
 */
public interface DimensionHandler {

    /**
     * 获取维度名称
     */
    String getDimensionName();

    /**
     * 计算维度因子
     *
     * @param snapshot 上下文快照
     * @param request  决策请求
     * @return 维度因子
     */
    DimensionFactor calculate(ContextSnapshot snapshot, DecisionRequest request);

    /**
     * 获取维度权重
     * 默认权重为1.0
     */
    default double getWeight() {
        return 1.0;
    }

    /**
     * 获取维度优先级
     * 数值越小优先级越高
     */
    default int getPriority() {
        return 100;
    }
}

package com.example.demo.permission.core;

import com.example.demo.permission.context.ContextSnapshot;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 上下文快照采集器接口
 */
public interface ContextSnapshotCollector {

    /**
     * 采集当前请求的上下文快照
     *
     * @param request HTTP请求
     * @param userId  用户ID
     * @return 上下文快照
     */
    ContextSnapshot collect(HttpServletRequest request, Long userId);

    /**
     * 异步采集上下文快照
     *
     * @param request HTTP请求
     * @param userId  用户ID
     */
    void collectAsync(HttpServletRequest request, Long userId);

    /**
     * 获取指定维度的数据
     *
     * @param dimension 维度名称
     * @param type      数据类型
     * @param <T>       泛型
     * @return 维度数据
     */
    <T> T getDimensionData(String dimension, Class<T> type);
}

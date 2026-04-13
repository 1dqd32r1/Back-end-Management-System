package com.example.demo.permission.context;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程本地上下文持有者
 * 用于在请求处理过程中传递上下文快照
 */
@Slf4j
public class ThreadLocalContextHolder {

    private static final ThreadLocal<ContextSnapshot> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 设置上下文快照
     */
    public static void set(ContextSnapshot snapshot) {
        CONTEXT_HOLDER.set(snapshot);
        log.debug("Context snapshot set: {}", snapshot != null ? snapshot.getSnapshotId() : "null");
    }

    /**
     * 获取上下文快照
     */
    public static ContextSnapshot get() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        ContextSnapshot snapshot = get();
        return snapshot != null ? snapshot.getUserId() : null;
    }

    /**
     * 获取会话ID
     */
    public static String getSessionId() {
        ContextSnapshot snapshot = get();
        return snapshot != null ? snapshot.getSessionId() : null;
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        CONTEXT_HOLDER.remove();
        log.debug("Context snapshot cleared");
    }

    /**
     * 检查是否存在上下文
     */
    public static boolean hasContext() {
        return CONTEXT_HOLDER.get() != null;
    }
}

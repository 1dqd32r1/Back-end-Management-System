package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * 权限决策请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRequest {

    /** 用户ID */
    private Long userId;

    /** 会话ID */
    private String sessionId;

    /** 资源类型 */
    private String resourceType;

    /** 资源ID */
    private String resourceId;

    /** 操作类型 */
    private String operation;

    /** 额外上下文 */
    private Map<String, Object> context;

    /** 请求ID */
    private String requestId;

    /**
     * 创建请求（自动生成请求ID）
     */
    public static DecisionRequest create(Long userId, String resourceType, String operation) {
        return DecisionRequest.builder()
                .userId(userId)
                .resourceType(resourceType)
                .operation(operation)
                .requestId(UUID.randomUUID().toString())
                .build();
    }

    /**
     * 创建带资源ID的请求
     */
    public static DecisionRequest create(Long userId, String resourceType, String resourceId, String operation) {
        return DecisionRequest.builder()
                .userId(userId)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .operation(operation)
                .requestId(UUID.randomUUID().toString())
                .build();
    }
}

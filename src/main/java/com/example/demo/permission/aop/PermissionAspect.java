package com.example.demo.permission.aop;

import com.example.demo.permission.annotation.SmartPermission;
import com.example.demo.permission.annotation.TrackBehavior;
import com.example.demo.permission.context.*;
import com.example.demo.permission.core.ContextSnapshotCollector;
import com.example.demo.permission.core.PermissionDecisionEngine;
import com.example.demo.permission.entity.PermBehaviorEvent;
import com.example.demo.permission.entity.PermContextSnapshot;
import com.example.demo.permission.mapper.PermBehaviorEventMapper;
import com.example.demo.permission.mapper.PermContextSnapshotMapper;
import com.example.demo.permission.mapper.PermDecisionLogMapper;
import com.example.demo.security.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 权限拦截切面
 * 在方法执行前进行智能权限检查
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final ContextSnapshotCollector contextCollector;
    private final PermissionDecisionEngine decisionEngine;
    private final PermContextSnapshotMapper snapshotMapper;
    private final PermDecisionLogMapper decisionLogMapper;
    private final PermBehaviorEventMapper behaviorEventMapper;
    private final ObjectMapper objectMapper;

    @Around("@annotation(smartPermission)")
    public Object checkPermission(ProceedingJoinPoint pjp, SmartPermission smartPermission) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取当前请求
        HttpServletRequest request = getCurrentRequest();
        Long userId = getCurrentUserId();

        if (userId == null) {
            throw new SecurityException("未登录或会话已过期");
        }

        // 采集上下文快照
        ContextSnapshot snapshot = contextCollector.collect(request, userId);

        // 设置到ThreadLocal
        ThreadLocalContextHolder.set(snapshot);

        try {
            // 构建决策请求
            DecisionRequest decisionRequest = DecisionRequest.builder()
                    .userId(userId)
                    .sessionId(snapshot.getSessionId())
                    .resourceType(smartPermission.resource())
                    .operation(smartPermission.operation())
                    .requestId(snapshot.getRequestId())
                    .build();

            // 执行权限决策
            DecisionResult result = decisionEngine.decide(decisionRequest);

            // 保存决策日志
            saveDecisionLog(snapshot, decisionRequest, result);

            // 处理决策结果
            if (result.isDenied()) {
                String message = smartPermission.denialMessage().isEmpty()
                        ? result.getExplanation()
                        : smartPermission.denialMessage();
                throw new SecurityException(message != null ? message : "权限不足");
            }

            // 执行原方法
            Object returnValue = pjp.proceed();

            // 记录成功的行为事件
            if (smartPermission.trackBehavior()) {
                recordBehaviorEvent(userId, smartPermission.resource(), smartPermission.operation(), "SUCCESS", null);
            }

            return returnValue;

        } finally {
            // 清理ThreadLocal
            ThreadLocalContextHolder.clear();

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Permission check completed in {}ms for {}.{}",
                    duration,
                    pjp.getTarget().getClass().getSimpleName(),
                    ((MethodSignature) pjp.getSignature()).getMethod().getName());
        }
    }

    @Around("@annotation(trackBehavior)")
    public Object trackBehavior(ProceedingJoinPoint pjp, TrackBehavior trackBehavior) throws Throwable {
        Long userId = getCurrentUserId();
        String methodName = ((MethodSignature) pjp.getSignature()).getMethod().getName();

        try {
            Object returnValue = pjp.proceed();

            // 记录行为事件
            if (userId != null) {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("methodName", methodName);
                eventData.put("args", Arrays.toString(pjp.getArgs()));
                eventData.put("eventType", trackBehavior.eventType());
                eventData.put("description", trackBehavior.description());

                recordBehaviorEvent(userId, "METHOD", methodName, "SUCCESS", eventData);
            }

            return returnValue;

        } catch (Exception e) {
            // 记录失败的行为事件
            if (userId != null) {
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("error", e.getMessage());
                recordBehaviorEvent(userId, "METHOD", methodName, "FAILURE", eventData);
            }
            throw e;
        }
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser.getUser().getUserId();
        }
        return null;
    }

    /**
     * 保存决策日志
     */
    private void saveDecisionLog(ContextSnapshot snapshot, DecisionRequest request, DecisionResult result) {
        try {
            // 保存上下文快照
            PermContextSnapshot snapshotEntity = new PermContextSnapshot();
            snapshotEntity.setUserId(snapshot.getUserId());
            snapshotEntity.setSessionId(snapshot.getSessionId());
            snapshotEntity.setIpAddress(snapshot.getSpatiotemporal() != null ? snapshot.getSpatiotemporal().getIpAddress() : null);
            snapshotEntity.setRequestId(snapshot.getRequestId());
            snapshotEntity.setDecisionResult(result.getDecision().name());
            snapshotEntity.setDecisionLatencyMs(result.getLatencyMs() != null ? result.getLatencyMs().intValue() : 0);
            snapshotEntity.setCreatedAt(LocalDateTime.now());
            snapshotMapper.insert(snapshotEntity);

        } catch (Exception e) {
            log.error("Failed to save decision log", e);
        }
    }

    /**
     * 记录行为事件
     */
    private void recordBehaviorEvent(Long userId, String resourceType, String operation, String status, Map<String, Object> eventData) {
        try {
            PermBehaviorEvent event = new PermBehaviorEvent();
            event.setUserId(userId);
            event.setEventType(resourceType + "_" + operation);
            event.setEventSource("WEB");
            event.setResourceType(resourceType);
            event.setOperation(operation);
            if (eventData != null) {
                event.setEventData(objectMapper.writeValueAsString(eventData));
            }
            event.setEventTime(LocalDateTime.now());
            event.setIsAnomaly(0);
            event.setProcessed(0);
            event.setCreatedAt(LocalDateTime.now());
            behaviorEventMapper.insert(event);
        } catch (Exception e) {
            log.error("Failed to record behavior event", e);
        }
    }
}

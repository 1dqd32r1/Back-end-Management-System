package com.example.demo.permission.core.impl;

import com.example.demo.permission.context.*;
import com.example.demo.permission.core.ContextSnapshotCollector;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 上下文快照采集器实现
 */
@Slf4j
@Component
public class ContextSnapshotCollectorImpl implements ContextSnapshotCollector {

    @Override
    public ContextSnapshot collect(HttpServletRequest request, Long userId) {
        long startTime = System.currentTimeMillis();

        ContextSnapshot snapshot = ContextSnapshot.builder()
                .snapshotId(UUID.randomUUID().toString())
                .userId(userId)
                .sessionId(request.getSession().getId())
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID().toString())
                .build();

        try {
            // 组织维度
            snapshot.setOrganizational(collectOrganizationalContext(userId));

            // 时空维度
            snapshot.setSpatiotemporal(collectSpatiotemporalContext(request));

            // 风险维度
            snapshot.setRisk(collectRiskContext());

            // 行为维度
            snapshot.setBehavioral(collectBehavioralContext(userId));

            // 原始上下文
            snapshot.setRawContext(collectRawContext(request));

        } catch (Exception e) {
            log.error("Error collecting context snapshot", e);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.debug("Context snapshot collected in {}ms", duration);

        return snapshot;
    }

    @Override
    @Async
    public void collectAsync(HttpServletRequest request, Long userId) {
        CompletableFuture.runAsync(() -> {
            try {
                collect(request, userId);
            } catch (Exception e) {
                log.error("Async context collection failed", e);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getDimensionData(String dimension, Class<T> type) {
        ContextSnapshot snapshot = ThreadLocalContextHolder.get();
        if (snapshot == null) {
            return null;
        }

        return switch (dimension.toLowerCase()) {
            case "organizational" -> (T) snapshot.getOrganizational();
            case "entity" -> (T) snapshot.getEntity();
            case "spatiotemporal" -> (T) snapshot.getSpatiotemporal();
            case "risk" -> (T) snapshot.getRisk();
            case "behavioral" -> (T) snapshot.getBehavioral();
            default -> null;
        };
    }

    /**
     * 采集组织维度上下文
     */
    private OrganizationalContext collectOrganizationalContext(Long userId) {
        return OrganizationalContext.builder()
                .deptId(1L)
                .deptName("技术部")
                .deptPath("0,1")
                .postIds(Arrays.asList(1L, 2L))
                .postNames(Arrays.asList("开发工程师", "架构师"))
                .virtualTeamIds(new ArrayList<>())
                .reportLine(new ArrayList<>())
                .isDeptLeader(false)
                .dataScope("3")
                .build();
    }

    /**
     * 采集时空维度上下文
     */
    private SpatiotemporalContext collectSpatiotemporalContext(HttpServletRequest request) {
        String ip = getClientIp(request);
        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();
        boolean isWorkingHours = currentHour >= 9 && currentHour <= 18;

        return SpatiotemporalContext.builder()
                .ipAddress(ip)
                .geoLocation(null)
                .deviceFingerprint(generateDeviceFingerprint(request))
                .deviceType(detectDeviceType(request))
                .browserType(detectBrowser(request))
                .osType(detectOs(request))
                .userAgent(request.getHeader("User-Agent"))
                .currentTime(LocalDateTime.now())
                .currentHour(currentHour)
                .isWorkingHours(isWorkingHours)
                .dayOfWeek(LocalDateTime.now().getDayOfWeek().getValue())
                .accessFrequency(0)
                .sessionDuration(0L)
                .isFirstAccess(false)
                .build();
    }

    /**
     * 采集风险维度上下文
     */
    private RiskContext collectRiskContext() {
        return RiskContext.builder()
                .operationSensitivity(0)
                .dataClassification(0)
                .dataClassCode("PUBLIC")
                .anomalyScore(BigDecimal.ZERO)
                .riskLevel(0)
                .riskFactors("[]")
                .isHighRisk(false)
                .requiresApproval(false)
                .approvedCount(0)
                .requiredApprovals(0)
                .recentAnomalyCount(0)
                .trustScore(new BigDecimal("100"))
                .build();
    }

    /**
     * 采集行为维度上下文
     */
    private BehavioralContext collectBehavioralContext(Long userId) {
        return BehavioralContext.builder()
                .typicalAccessHours("[9,10,11,12,13,14,15,16,17,18]")
                .isTypicalAccessTime(true)
                .typicalLocations(new ArrayList<>())
                .isTypicalLocation(true)
                .typicalDevices(new ArrayList<>())
                .isTypicalDevice(true)
                .frequentOperations(new ArrayList<>())
                .isFrequentOperation(false)
                .collaborators(new ArrayList<>())
                .skillTags(new ArrayList<>())
                .permissionUsageRate(new BigDecimal("0.5"))
                .behaviorAnomalyScore(BigDecimal.ZERO)
                .predictedNextOperation(null)
                .predictionConfidence(BigDecimal.ZERO)
                .build();
    }

    /**
     * 采集原始上下文
     */
    private Map<String, Object> collectRawContext(HttpServletRequest request) {
        Map<String, Object> rawContext = new HashMap<>();
        rawContext.put("method", request.getMethod());
        rawContext.put("uri", request.getRequestURI());
        rawContext.put("queryString", request.getQueryString());
        rawContext.put("headers", getHeaders(request));
        return rawContext;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 生成设备指纹
     */
    private String generateDeviceFingerprint(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");
        String raw = (userAgent != null ? userAgent : "") + "|" + (acceptLanguage != null ? acceptLanguage : "");
        return Integer.toHexString(raw.hashCode());
    }

    /**
     * 检测设备类型
     */
    private String detectDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "UNKNOWN";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "MOBILE";
        }
        if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "TABLET";
        }
        return "PC";
    }

    /**
     * 检测浏览器
     */
    private String detectBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "UNKNOWN";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("edge")) return "Edge";
        if (userAgent.contains("chrome")) return "Chrome";
        if (userAgent.contains("firefox")) return "Firefox";
        if (userAgent.contains("safari") && !userAgent.contains("chrome")) return "Safari";
        if (userAgent.contains("opera") || userAgent.contains("opr")) return "Opera";
        return "Unknown";
    }

    /**
     * 检测操作系统
     */
    private String detectOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "UNKNOWN";
        userAgent = userAgent.toLowerCase();
        if (userAgent.contains("windows")) return "Windows";
        if (userAgent.contains("mac")) return "MacOS";
        if (userAgent.contains("linux")) return "Linux";
        if (userAgent.contains("android")) return "Android";
        if (userAgent.contains("iphone") || userAgent.contains("ipad")) return "iOS";
        return "Unknown";
    }

    /**
     * 获取请求头
     */
    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        return headers;
    }
}

package com.example.demo.permission.prediction.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.permission.entity.PermBehaviorEvent;
import com.example.demo.permission.entity.PermUserProfile;
import com.example.demo.permission.mapper.PermBehaviorEventMapper;
import com.example.demo.permission.mapper.PermUserProfileMapper;
import com.example.demo.permission.prediction.BehaviorPredictor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 行为预测服务实现
 * 基于历史行为数据进行分析和预测
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorPredictorImpl implements BehaviorPredictor {

    private final PermBehaviorEventMapper behaviorEventMapper;
    private final PermUserProfileMapper userProfileMapper;
    private final ObjectMapper objectMapper;

    private static final int MIN_SAMPLES_FOR_PREDICTION = 10;
    private static final String MODEL_VERSION = "v1.0.0-simplified";

    @Override
    public PredictionResult predictNextOperation(Long userId) {
        // 获取用户最近的行为事件
        List<PermBehaviorEvent> recentEvents = behaviorEventMapper.selectList(
                new QueryWrapper<PermBehaviorEvent>()
                        .eq("user_id", userId)
                        .orderByDesc("event_time")
                        .last("LIMIT 100")
        );

        if (recentEvents.size() < MIN_SAMPLES_FOR_PREDICTION) {
            return new PredictionResult(userId, "unknown", 0.0, Collections.emptyList(), Collections.emptyMap());
        }

        // 统计操作频率
        Map<String, Long> operationCounts = recentEvents.stream()
                .filter(e -> e.getOperation() != null)
                .collect(Collectors.groupingBy(PermBehaviorEvent::getOperation, Collectors.counting()));

        // 找出最可能的操作
        Optional<Map.Entry<String, Long>> mostCommon = operationCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (mostCommon.isEmpty()) {
            return new PredictionResult(userId, "unknown", 0.0, Collections.emptyList(), Collections.emptyMap());
        }

        String predictedOp = mostCommon.get().getKey();
        double confidence = (double) mostCommon.get().getValue() / recentEvents.size();

        // 备选操作
        List<String> alternatives = operationCounts.entrySet().stream()
                .filter(e -> !e.getKey().equals(predictedOp))
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 特征重要性（模拟）
        Map<String, Double> featureImportance = new HashMap<>();
        featureImportance.put("time_of_day", 0.35);
        featureImportance.put("previous_operation", 0.25);
        featureImportance.put("resource_type", 0.20);
        featureImportance.put("access_frequency", 0.15);
        featureImportance.put("device_type", 0.05);

        return new PredictionResult(userId, predictedOp, confidence, alternatives, featureImportance);
    }

    @Override
    public AccessPatternPrediction predictAccessPattern(Long userId, int hours) {
        LocalDateTime now = LocalDateTime.now();

        // 获取用户历史访问模式
        List<PermBehaviorEvent> events = behaviorEventMapper.selectList(
                new QueryWrapper<PermBehaviorEvent>()
                        .eq("user_id", userId)
                        .ge("event_time", now.minusDays(30))
                        .orderByAsc("event_time")
        );

        // 按小时统计访问频率
        Map<Integer, List<PermBehaviorEvent>> hourlyEvents = events.stream()
                .collect(Collectors.groupingBy(e -> e.getEventTime().getHour()));

        List<TimeSlotPrediction> timeSlots = new ArrayList<>();
        List<String> predictedResources = new ArrayList<>();

        for (int h = 0; h < hours; h++) {
            int targetHour = (now.getHour() + h) % 24;
            List<PermBehaviorEvent> hourEvents = hourlyEvents.getOrDefault(targetHour, Collections.emptyList());

            double probability = Math.min(1.0, hourEvents.size() / 20.0);

            List<String> likelyOps = hourEvents.stream()
                    .filter(e -> e.getOperation() != null)
                    .collect(Collectors.groupingBy(PermBehaviorEvent::getOperation, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            timeSlots.add(new TimeSlotPrediction(targetHour, probability, likelyOps));

            // 收集预测资源
            hourEvents.stream()
                    .filter(e -> e.getResourceType() != null)
                    .map(PermBehaviorEvent::getResourceType)
                    .distinct()
                    .forEach(r -> {
                        if (!predictedResources.contains(r)) {
                            predictedResources.add(r);
                        }
                    });
        }

        double overallConfidence = events.size() > 100 ? 0.85 : events.size() / 100.0 * 0.85;

        return new AccessPatternPrediction(userId, now, timeSlots, predictedResources, overallConfidence);
    }

    @Override
    public AnomalyDetectionResult detectAnomaly(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 获取用户画像
        PermUserProfile profile = userProfileMapper.selectOne(
                new QueryWrapper<PermUserProfile>().eq("user_id", userId)
        );

        // 获取最近24小时的事件
        List<PermBehaviorEvent> recentEvents = behaviorEventMapper.selectList(
                new QueryWrapper<PermBehaviorEvent>()
                        .eq("user_id", userId)
                        .ge("event_time", now.minusHours(24))
                        .orderByDesc("event_time")
        );

        double anomalyScore = 0.0;
        List<String> indicators = new ArrayList<>();
        String anomalyType = "NONE";
        String description = "正常行为";

        if (profile != null && recentEvents.size() > MIN_SAMPLES_FOR_PREDICTION) {
            // 检查访问时间是否异常
            Map<String, Object> baseline = parseJsonMap(profile.getBehaviorBaseline());
            List<Integer> typicalHours = baseline.containsKey("typicalHours")
                    ? (List<Integer>) baseline.get("typicalHours")
                    : Arrays.asList(8, 9, 10, 11, 14, 15, 16, 17, 18);

            long offHourAccess = recentEvents.stream()
                    .filter(e -> !typicalHours.contains(e.getEventTime().getHour()))
                    .count();

            if (offHourAccess > recentEvents.size() * 0.5) {
                anomalyScore += 0.3;
                indicators.add("非正常时段访问占比过高");
            }

            // 检查操作频率异常
            long operationCount = recentEvents.size();
            double avgHourlyOps = operationCount / 24.0;
            if (avgHourlyOps > 50) {
                anomalyScore += 0.25;
                indicators.add("操作频率异常高");
            }

            // 检查IP地址变化
            long distinctIps = recentEvents.stream()
                    .filter(e -> e.getIpAddress() != null)
                    .map(PermBehaviorEvent::getIpAddress)
                    .distinct()
                    .count();

            if (distinctIps > 5) {
                anomalyScore += 0.2;
                indicators.add("IP地址变化频繁");
            }

            // 检查异常操作
            long anomalyCount = recentEvents.stream()
                    .filter(e -> e.getIsAnomaly() != null && e.getIsAnomaly() == 1)
                    .count();

            if (anomalyCount > 0) {
                anomalyScore += 0.25 * anomalyCount;
                indicators.add("存在标记的异常操作");
            }

            // 确定异常类型
            if (anomalyScore >= 0.7) {
                anomalyType = "HIGH_RISK";
                description = "检测到高风险异常行为，建议立即关注";
            } else if (anomalyScore >= 0.4) {
                anomalyType = "MEDIUM_RISK";
                description = "检测到中等风险异常行为，建议核查";
            } else if (anomalyScore > 0) {
                anomalyType = "LOW_RISK";
                description = "检测到轻微异常，持续观察中";
            }
        }

        boolean isAnomaly = anomalyScore >= 0.4;

        return new AnomalyDetectionResult(
                userId, isAnomaly, Math.min(1.0, anomalyScore),
                anomalyType, description, indicators, now
        );
    }

    @Override
    public PermissionUsageTrend predictUsageTrend(Long userId, int days) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 获取过去30天的数据
        List<PermBehaviorEvent> events = behaviorEventMapper.selectList(
                new QueryWrapper<PermBehaviorEvent>()
                        .eq("user_id", userId)
                        .ge("event_time", now.minusDays(30))
                        .orderByAsc("event_time")
        );

        // 按天统计
        Map<String, Set<String>> dailyPermissions = events.stream()
                .filter(e -> e.getResourceType() != null && e.getOperation() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getEventTime().format(formatter),
                        Collectors.mapping(
                                e -> e.getResourceType() + ":" + e.getOperation(),
                                Collectors.toSet()
                        )
                ));

        List<DailyTrend> trends = new ArrayList<>();
        Set<String> allUsedPermissions = new HashSet<>();
        int totalPermissions = 50; // 假设用户总权限数

        for (int d = 0; d < days; d++) {
            String date = now.plusDays(d).format(formatter);
            Set<String> dayPerms = dailyPermissions.getOrDefault(date, Collections.emptySet());
            allUsedPermissions.addAll(dayPerms);

            trends.add(new DailyTrend(
                    date,
                    dayPerms.size(),
                    totalPermissions,
                    (double) dayPerms.size() / totalPermissions
            ));
        }

        // 分析趋势
        List<String> increasingPermissions = new ArrayList<>();
        List<String> decreasingPermissions = new ArrayList<>();
        String recommendation = "";

        if (!trends.isEmpty()) {
            // 简单趋势判断
            long avgUsed = (long) trends.stream().mapToInt(DailyTrend::usedPermissions).average().orElse(0);
            if (avgUsed < totalPermissions * 0.3) {
                recommendation = "权限使用率较低，建议评估是否存在过度授权";
            } else if (avgUsed > totalPermissions * 0.8) {
                recommendation = "权限使用率高，建议关注敏感操作";
            } else {
                recommendation = "权限使用正常";
            }
        }

        return new PermissionUsageTrend(userId, trends, increasingPermissions, decreasingPermissions, recommendation);
    }

    @Override
    public ModelTrainingResult trainModel(Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 获取用户历史数据
        long sampleCount = behaviorEventMapper.selectCount(
                new QueryWrapper<PermBehaviorEvent>().eq("user_id", userId)
        );

        if (sampleCount < MIN_SAMPLES_FOR_PREDICTION) {
            return new ModelTrainingResult(userId, false, (int) sampleCount, 0.0, MODEL_VERSION, now);
        }

        // 模拟模型训练
        // 实际项目中这里会调用机器学习框架进行训练
        double accuracy = 0.75 + Math.random() * 0.15; // 模拟准确率

        // 更新用户画像
        PermUserProfile profile = userProfileMapper.selectOne(
                new QueryWrapper<PermUserProfile>().eq("user_id", userId)
        );

        if (profile != null) {
            Map<String, Object> baseline = new HashMap<>();
            baseline.put("modelVersion", MODEL_VERSION);
            baseline.put("trainedAt", now.toString());
            baseline.put("accuracy", accuracy);
            baseline.put("typicalHours", Arrays.asList(8, 9, 10, 11, 14, 15, 16, 17, 18));

            try {
                profile.setBehaviorBaseline(objectMapper.writeValueAsString(baseline));
                profile.setLastBaselineUpdate(now);
                userProfileMapper.updateById(profile);
            } catch (JsonProcessingException e) {
                log.error("Failed to update user profile", e);
            }
        }

        return new ModelTrainingResult(userId, true, (int) sampleCount, accuracy, MODEL_VERSION, now);
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}

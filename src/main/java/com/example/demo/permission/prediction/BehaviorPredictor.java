package com.example.demo.permission.prediction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 行为预测服务接口
 */
public interface BehaviorPredictor {

    /**
     * 预测用户下一个操作
     * @param userId 用户ID
     * @return 预测结果
     */
    PredictionResult predictNextOperation(Long userId);

    /**
     * 预测用户访问模式
     * @param userId 用户ID
     * @param hours 预测未来几小时
     * @return 访问模式预测
     */
    AccessPatternPrediction predictAccessPattern(Long userId, int hours);

    /**
     * 异常行为检测
     * @param userId 用户ID
     * @return 异常检测结果
     */
    AnomalyDetectionResult detectAnomaly(Long userId);

    /**
     * 权限使用趋势预测
     * @param userId 用户ID
     * @param days 预测未来几天
     * @return 使用趋势
     */
    PermissionUsageTrend predictUsageTrend(Long userId, int days);

    /**
     * 训练预测模型
     * @param userId 用户ID
     * @return 训练结果
     */
    ModelTrainingResult trainModel(Long userId);

    // ==================== 结果类 ====================

    record PredictionResult(
            Long userId,
            String predictedOperation,
            double confidence,
            List<String> alternativeOperations,
            Map<String, Double> featureImportance
    ) {}

    record AccessPatternPrediction(
            Long userId,
            LocalDateTime predictionTime,
            List<TimeSlotPrediction> timeSlots,
            List<String> predictedResources,
            double overallConfidence
    ) {}

    record TimeSlotPrediction(
            int hour,
            double accessProbability,
            List<String> likelyOperations
    ) {}

    record AnomalyDetectionResult(
            Long userId,
            boolean isAnomaly,
            double anomalyScore,
            String anomalyType,
            String description,
            List<String> indicators,
            LocalDateTime detectionTime
    ) {}

    record PermissionUsageTrend(
            Long userId,
            List<DailyTrend> trends,
            List<String> increasingPermissions,
            List<String> decreasingPermissions,
            String recommendation
    ) {}

    record DailyTrend(
            String date,
            int usedPermissions,
            int totalPermissions,
            double usageRate
    ) {}

    record ModelTrainingResult(
            Long userId,
            boolean success,
            int samplesUsed,
            double accuracy,
            String modelVersion,
            LocalDateTime trainedAt
    ) {}
}

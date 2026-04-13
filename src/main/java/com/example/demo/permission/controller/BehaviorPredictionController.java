package com.example.demo.permission.controller;

import com.example.demo.common.Result;
import com.example.demo.permission.prediction.BehaviorPredictor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 行为预测控制器
 */
@RestController
@RequestMapping("/api/permission/prediction")
@RequiredArgsConstructor
public class BehaviorPredictionController {

    private final BehaviorPredictor behaviorPredictor;

    /**
     * 预测用户下一个操作
     */
    @GetMapping("/next-operation/{userId}")
    public Result<BehaviorPredictor.PredictionResult> predictNextOperation(@PathVariable Long userId) {
        BehaviorPredictor.PredictionResult result = behaviorPredictor.predictNextOperation(userId);
        return Result.success(result);
    }

    /**
     * 预测访问模式
     */
    @GetMapping("/access-pattern/{userId}")
    public Result<BehaviorPredictor.AccessPatternPrediction> predictAccessPattern(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "24") Integer hours) {

        BehaviorPredictor.AccessPatternPrediction prediction = behaviorPredictor.predictAccessPattern(userId, hours);
        return Result.success(prediction);
    }

    /**
     * 异常检测
     */
    @GetMapping("/anomaly/{userId}")
    public Result<BehaviorPredictor.AnomalyDetectionResult> detectAnomaly(@PathVariable Long userId) {
        BehaviorPredictor.AnomalyDetectionResult result = behaviorPredictor.detectAnomaly(userId);
        return Result.success(result);
    }

    /**
     * 权限使用趋势预测
     */
    @GetMapping("/usage-trend/{userId}")
    public Result<BehaviorPredictor.PermissionUsageTrend> predictUsageTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "7") Integer days) {

        BehaviorPredictor.PermissionUsageTrend trend = behaviorPredictor.predictUsageTrend(userId, days);
        return Result.success(trend);
    }

    /**
     * 训练预测模型
     */
    @PostMapping("/train/{userId}")
    public Result<BehaviorPredictor.ModelTrainingResult> trainModel(@PathVariable Long userId) {
        BehaviorPredictor.ModelTrainingResult result = behaviorPredictor.trainModel(userId);
        return Result.success(result);
    }
}

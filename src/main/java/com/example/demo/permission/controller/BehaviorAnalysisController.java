package com.example.demo.permission.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.permission.entity.PermBehaviorEvent;
import com.example.demo.permission.entity.PermUserProfile;
import com.example.demo.permission.mapper.PermBehaviorEventMapper;
import com.example.demo.permission.mapper.PermUserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 行为分析控制器
 */
@RestController
@RequestMapping("/api/permission/behavior")
@RequiredArgsConstructor
public class BehaviorAnalysisController {

    private final PermUserProfileMapper userProfileMapper;
    private final PermBehaviorEventMapper behaviorEventMapper;

    /**
     * 获取用户行为画像
     */
    @GetMapping("/profile/{userId}")
    public Result<PermUserProfile> getProfile(@PathVariable Long userId) {
        PermUserProfile profile = userProfileMapper.selectOne(
                new QueryWrapper<PermUserProfile>().eq("user_id", userId));
        return Result.success(profile);
    }

    /**
     * 获取行为事件列表
     */
    @GetMapping("/events")
    public Result<Page<PermBehaviorEvent>> getEvents(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String eventType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {

        Page<PermBehaviorEvent> page = new Page<>(pageNum, pageSize);
        QueryWrapper<PermBehaviorEvent> wrapper = new QueryWrapper<>();

        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (eventType != null) {
            wrapper.eq("event_type", eventType);
        }
        wrapper.orderByDesc("event_time");

        behaviorEventMapper.selectPage(page, wrapper);
        return Result.success(page);
    }

    /**
     * 获取异常行为列表
     */
    @GetMapping("/anomaly")
    public Result<List<PermBehaviorEvent>> getAnomalies(
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "100") Integer limit) {

        QueryWrapper<PermBehaviorEvent> wrapper = new QueryWrapper<>();
        wrapper.eq("is_anomaly", 1);
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        wrapper.orderByDesc("event_time");
        wrapper.last("LIMIT " + limit);

        List<PermBehaviorEvent> events = behaviorEventMapper.selectList(wrapper);
        return Result.success(events);
    }

    /**
     * 行为统计
     */
    @GetMapping("/stats/{userId}")
    public Result<Map<String, Object>> getStats(@PathVariable Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 总事件数
        Long totalEvents = behaviorEventMapper.selectCount(
                new QueryWrapper<PermBehaviorEvent>().eq("user_id", userId));
        stats.put("totalEvents", totalEvents);

        // 异常事件数
        Long anomalyEvents = behaviorEventMapper.selectCount(
                new QueryWrapper<PermBehaviorEvent>()
                        .eq("user_id", userId)
                        .eq("is_anomaly", 1));
        stats.put("anomalyEvents", anomalyEvents);

        // 异常率
        double anomalyRate = totalEvents > 0 ? (double) anomalyEvents / totalEvents * 100 : 0;
        stats.put("anomalyRate", String.format("%.2f", anomalyRate) + "%");

        return Result.success(stats);
    }

    /**
     * 行为反馈（标记异常）
     */
    @PostMapping("/feedback")
    public Result<?> feedback(@RequestBody Map<String, Object> params) {
        Long eventId = Long.valueOf(params.get("eventId").toString());
        Boolean isAnomaly = Boolean.valueOf(params.get("isAnomaly").toString());
        String comment = (String) params.get("comment");

        PermBehaviorEvent event = behaviorEventMapper.selectById(eventId);
        if (event != null) {
            event.setIsAnomaly(isAnomaly ? 1 : 0);
            event.setProcessed(1);
            behaviorEventMapper.updateById(event);
        }

        return Result.success();
    }
}

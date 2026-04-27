package com.example.demo.controller;

import com.example.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Tag(name = "数据城市", description = "3D数据城市相关接口")
@RestController
@RequestMapping("/datacity")
public class DataCityController {

    @Operation(summary = "获取城市数据", description = "获取3D数据城市所需的数据")
    @GetMapping("/data")
    public Result<Map<String, Object>> getCityData() {
        Map<String, Object> result = new HashMap<>();

        // 建筑数据 - 模拟销售数据
        List<Map<String, Object>> buildings = new ArrayList<>();
        String[] departments = {"华东区", "华南区", "华北区", "西南区", "华中区", "西北区", "东北区", "东南区", "中原区", "沿海区"};
        Random random = new Random();

        for (int i = 0; i < departments.length; i++) {
            Map<String, Object> building = new HashMap<>();
            building.put("id", i + 1);
            building.put("name", departments[i]);
            // 销售额决定高度 (1000-10000万)
            double sales = 1000 + random.nextDouble() * 9000;
            building.put("sales", Math.round(sales * 100) / 100.0);
            // 高度映射：限制在2-12范围内，避免建筑过高
            double rawHeight = sales / 1200;
            double height = Math.max(2, Math.min(12, rawHeight));
            building.put("height", height);
            // 用户增长
            int userGrowth = 1000 + random.nextInt(9000);
            building.put("userGrowth", userGrowth);
            // 宽度映射：限制在1-2范围内
            double width = 1 + Math.min(1, userGrowth / 9000.0);
            building.put("width", width);
            // 是否异常
            boolean isAbnormal = random.nextDouble() < 0.15;
            building.put("isAbnormal", isAbnormal);
            building.put("color", isAbnormal ? "#ff4444" : getDefaultColor(i));

            // 详细数据
            Map<String, Object> details = new HashMap<>();
            details.put("orderCount", 500 + random.nextInt(2000));
            details.put("avgOrderValue", 200 + random.nextDouble() * 800);
            details.put("conversionRate", 1 + random.nextDouble() * 5);
            details.put("returnRate", random.nextDouble() * 10);
            details.put("topProducts", Arrays.asList("产品A", "产品B", "产品C"));
            building.put("details", details);

            buildings.add(building);
        }
        result.put("buildings", buildings);

        // 城市整体统计
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalSales", 58000.0);
        statistics.put("totalUsers", 150000);
        statistics.put("totalOrders", 12000);
        statistics.put("abnormalCount", 2);
        result.put("statistics", statistics);

        // 时间轴数据
        List<Map<String, Object>> timeline = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("month", month + "月");
            monthData.put("sales", 4000 + random.nextDouble() * 3000);
            monthData.put("users", 10000 + random.nextInt(5000));
            timeline.add(monthData);
        }
        result.put("timeline", timeline);

        return Result.success(result);
    }

    private String getDefaultColor(int index) {
        String[] colors = {"#4fc3f7", "#81c784", "#ffb74d", "#ba68c8", "#f06292",
                          "#4db6ac", "#7986cb", "#aed581", "#ff8a65", "#90a4ae"};
        return colors[index % colors.length];
    }
}

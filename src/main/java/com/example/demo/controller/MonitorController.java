package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.SystemMonitorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "系统监控", description = "系统监控相关接口")
@RestController
@RequestMapping("/monitor/server")
public class MonitorController {

    @Autowired
    private SystemMonitorService systemMonitorService;

    @Operation(summary = "获取服务器监控信息", description = "获取CPU、内存、磁盘等服务器信息")
    @GetMapping
    public Result<Map<String, Object>> getServerMonitor() {
        return Result.success(systemMonitorService.snapshot());
    }
}

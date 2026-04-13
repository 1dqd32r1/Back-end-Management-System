package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.service.SystemMonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/monitor/server")
public class MonitorController {

    @Autowired
    private SystemMonitorService systemMonitorService;

    @GetMapping
    public Result<Map<String, Object>> getServerMonitor() {
        return Result.success(systemMonitorService.snapshot());
    }
}

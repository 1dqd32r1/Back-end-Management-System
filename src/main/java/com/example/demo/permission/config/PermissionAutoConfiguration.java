package com.example.demo.permission.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 智能权限中枢自动配置
 */
@Configuration
@ComponentScan(basePackages = "com.example.demo.permission")
@EnableAsync
public class PermissionAutoConfiguration {
}

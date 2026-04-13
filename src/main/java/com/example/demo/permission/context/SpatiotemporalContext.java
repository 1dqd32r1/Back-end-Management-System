package com.example.demo.permission.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 时空维度上下文
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpatiotemporalContext {

    /** IP地址 */
    private String ipAddress;

    /** 地理位置（经纬度，如 "39.9042,116.4074"） */
    private String geoLocation;

    /** 城市 */
    private String city;

    /** 国家 */
    private String country;

    /** 设备指纹 */
    private String deviceFingerprint;

    /** 设备类型（PC/MOBILE/TABLET） */
    private String deviceType;

    /** 浏览器类型 */
    private String browserType;

    /** 操作系统 */
    private String osType;

    /** User-Agent */
    private String userAgent;

    /** 当前时间 */
    private LocalDateTime currentTime;

    /** 当前小时 */
    private Integer currentHour;

    /** 是否工作时间（9:00-18:00） */
    private Boolean isWorkingHours;

    /** 星期几（1-7） */
    private Integer dayOfWeek;

    /** 访问频率（次/小时） */
    private Integer accessFrequency;

    /** 会话持续时间（秒） */
    private Long sessionDuration;

    /** 是否首次访问 */
    private Boolean isFirstAccess;
}

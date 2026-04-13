package com.example.demo.permission.annotation;

import java.lang.annotation.*;

/**
 * 智能权限注解
 * 用于标注需要进行智能权限检查的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SmartPermission {

    /**
     * 资源类型
     */
    String resource();

    /**
     * 操作类型
     */
    String operation();

    /**
     * 风险等级 (0-4)
     */
    int riskLevel() default 0;

    /**
     * 数据分级 (PUBLIC/INTERNAL/CONFIDENTIAL/SECRET/TOP_SECRET)
     */
    String dataClassification() default "PUBLIC";

    /**
     * 是否记录行为日志
     */
    boolean trackBehavior() default true;

    /**
     * 是否启用时空维度检查
     */
    boolean checkSpatiotemporal() default true;

    /**
     * 是否启用风险维度检查
     */
    boolean checkRisk() default true;

    /**
     * 自定义拒绝消息
     */
    String denialMessage() default "";
}

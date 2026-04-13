package com.example.demo.permission.annotation;

import java.lang.annotation.*;

/**
 * 行为追踪注解
 * 用于标注需要记录用户行为的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TrackBehavior {

    /**
     * 事件类型
     */
    String eventType() default "OPERATION";

    /**
     * 事件描述
     */
    String description() default "";

    /**
     * 是否敏感操作
     */
    boolean sensitive() default false;
}

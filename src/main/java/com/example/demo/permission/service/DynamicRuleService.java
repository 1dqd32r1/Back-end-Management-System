package com.example.demo.permission.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;
import com.example.demo.permission.entity.PermDynamicRule;

import java.util.List;
import java.util.Map;

/**
 * 动态权限规则服务接口
 */
public interface DynamicRuleService extends IService<PermDynamicRule> {

    /**
     * 分页查询规则
     */
    Page<PermDynamicRule> queryPage(Map<String, Object> params);

    /**
     * 创建规则
     */
    PermDynamicRule createRule(PermDynamicRule rule);

    /**
     * 更新规则
     */
    PermDynamicRule updateRule(PermDynamicRule rule);

    /**
     * 启用规则
     */
    void enableRule(Long ruleId);

    /**
     * 禁用规则
     */
    void disableRule(Long ruleId);

    /**
     * 测试规则
     */
    DecisionResult testRule(Long ruleId, DecisionRequest request);

    /**
     * 获取所有启用的规则（按优先级排序）
     */
    List<PermDynamicRule> getActiveRules();

    /**
     * 根据类型获取规则
     */
    List<PermDynamicRule> getRulesByType(String ruleType);
}

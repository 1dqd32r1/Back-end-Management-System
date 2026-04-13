package com.example.demo.permission.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.common.Result;
import com.example.demo.permission.context.DecisionRequest;
import com.example.demo.permission.context.DecisionResult;
import com.example.demo.permission.entity.PermDynamicRule;
import com.example.demo.permission.service.DynamicRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 动态规则控制器
 */
@RestController
@RequestMapping("/api/permission/rule")
@RequiredArgsConstructor
public class DynamicRuleController {

    private final DynamicRuleService ruleService;

    /**
     * 获取规则列表
     */
    @GetMapping("/list")
    public Result<List<PermDynamicRule>> list(
            @RequestParam(required = false) String ruleType,
            @RequestParam(required = false) Integer status) {

        List<PermDynamicRule> rules;
        if (ruleType != null) {
            rules = ruleService.getRulesByType(ruleType);
        } else if (status != null) {
            rules = ruleService.list(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PermDynamicRule>()
                    .eq("status", status)
                    .orderByAsc("priority"));
        } else {
            rules = ruleService.getActiveRules();
        }
        return Result.success(rules);
    }

    /**
     * 分页查询规则
     */
    @GetMapping("/page")
    public Result<Page<PermDynamicRule>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String ruleType) {

        Page<PermDynamicRule> page = new Page<>(pageNum, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<PermDynamicRule> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        if (ruleType != null) {
            wrapper.eq("rule_type", ruleType);
        }
        wrapper.orderByAsc("priority");
        ruleService.page(page, wrapper);
        return Result.success(page);
    }

    /**
     * 获取规则详情
     */
    @GetMapping("/{ruleId}")
    public Result<PermDynamicRule> get(@PathVariable Long ruleId) {
        return Result.success(ruleService.getById(ruleId));
    }

    /**
     * 创建规则
     */
    @PostMapping("/create")
    public Result<PermDynamicRule> create(@RequestBody PermDynamicRule rule) {
        return Result.success(ruleService.createRule(rule));
    }

    /**
     * 更新规则
     */
    @PutMapping("/{ruleId}")
    public Result<PermDynamicRule> update(@PathVariable Long ruleId, @RequestBody PermDynamicRule rule) {
        rule.setRuleId(ruleId);
        return Result.success(ruleService.updateRule(rule));
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/{ruleId}")
    public Result<?> delete(@PathVariable Long ruleId) {
        ruleService.removeById(ruleId);
        return Result.success();
    }

    /**
     * 启用规则
     */
    @PostMapping("/{ruleId}/enable")
    public Result<?> enable(@PathVariable Long ruleId) {
        ruleService.enableRule(ruleId);
        return Result.success();
    }

    /**
     * 禁用规则
     */
    @PostMapping("/{ruleId}/disable")
    public Result<?> disable(@PathVariable Long ruleId) {
        ruleService.disableRule(ruleId);
        return Result.success();
    }

    /**
     * 测试规则
     */
    @PostMapping("/test")
    public Result<DecisionResult> test(@RequestBody Map<String, Object> params) {
        Long ruleId = Long.valueOf(params.get("ruleId").toString());
        @SuppressWarnings("unchecked")
        Map<String, Object> testContext = (Map<String, Object>) params.get("testContext");

        DecisionRequest request = DecisionRequest.builder()
                .userId(Long.valueOf(testContext.get("userId").toString()))
                .resourceType((String) testContext.get("resourceType"))
                .operation((String) testContext.get("operation"))
                .build();

        DecisionResult result = ruleService.testRule(ruleId, request);
        return Result.success(result);
    }
}

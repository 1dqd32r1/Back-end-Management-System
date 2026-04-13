package com.example.demo.permission.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.permission.entity.PermDataClassification;
import com.example.demo.permission.entity.PermDynamicRule;
import com.example.demo.permission.mapper.PermDataClassificationMapper;
import com.example.demo.permission.mapper.PermDynamicRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 权限系统初始化器
 * 创建默认规则和数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionInitializer implements CommandLineRunner {

    private final PermDynamicRuleMapper ruleMapper;
    private final PermDataClassificationMapper classificationMapper;

    @Override
    public void run(String... args) {
        initDefaultRules();
        log.info(">>> 智能权限中枢系统初始化完成");
    }

    private void initDefaultRules() {
        // 检查是否已有规则
        Long count = ruleMapper.selectCount(new QueryWrapper<>());
        if (count > 0) {
            log.info(">>> 规则已存在，跳过初始化");
            return;
        }

        // 创建默认规则

        // 1. 非工作时间访问限制
        PermDynamicRule afterHoursRule = new PermDynamicRule();
        afterHoursRule.setRuleName("非工作时间访问限制");
        afterHoursRule.setRuleCode("RULE_AFTER_HOURS");
        afterHoursRule.setRuleType("TIME");
        afterHoursRule.setDimension("SPATIOTEMPORAL");
        afterHoursRule.setConditionExpression("#time.currentHour < 9 or #time.currentHour > 18");
        afterHoursRule.setActionType("DEGRADE");
        afterHoursRule.setPriority(100);
        afterHoursRule.setStatus(1);
        afterHoursRule.setDescription("非工作时间（9:00-18:00之外）降低权限级别");
        afterHoursRule.setCreatedAt(LocalDateTime.now());
        ruleMapper.insert(afterHoursRule);

        // 2. 高风险操作限制
        PermDynamicRule highRiskRule = new PermDynamicRule();
        highRiskRule.setRuleName("高风险操作限制");
        highRiskRule.setRuleCode("RULE_HIGH_RISK");
        highRiskRule.setRuleType("RISK");
        highRiskRule.setDimension("RISK");
        highRiskRule.setConditionExpression("#risk.riskLevel >= 3");
        highRiskRule.setActionType("DENY");
        highRiskRule.setPriority(10);
        highRiskRule.setStatus(1);
        highRiskRule.setDescription("风险等级>=3的操作直接拒绝");
        highRiskRule.setCreatedAt(LocalDateTime.now());
        ruleMapper.insert(highRiskRule);

        // 3. 异常行为限制
        PermDynamicRule anomalyRule = new PermDynamicRule();
        anomalyRule.setRuleName("异常行为限制");
        anomalyRule.setRuleCode("RULE_ANOMALY");
        anomalyRule.setRuleType("BEHAVIOR");
        anomalyRule.setDimension("RISK");
        anomalyRule.setConditionExpression("#risk.anomalyScore > 70");
        anomalyRule.setActionType("DENY");
        anomalyRule.setPriority(5);
        anomalyRule.setStatus(1);
        anomalyRule.setDescription("异常评分>70的操作直接拒绝");
        anomalyRule.setCreatedAt(LocalDateTime.now());
        ruleMapper.insert(anomalyRule);

        // 4. 机密数据访问限制
        PermDynamicRule confidentialRule = new PermDynamicRule();
        confidentialRule.setRuleName("机密数据访问限制");
        confidentialRule.setRuleCode("RULE_CONFIDENTIAL");
        confidentialRule.setRuleType("RISK");
        confidentialRule.setDimension("RISK");
        confidentialRule.setConditionExpression("#risk.dataClassification >= 2 and #risk.requiresApproval == true and #risk.approvedCount < #risk.requiredApprovals");
        confidentialRule.setActionType("DENY");
        confidentialRule.setPriority(15);
        confidentialRule.setStatus(1);
        confidentialRule.setDescription("机密及以上级别数据需要审批后才能访问");
        confidentialRule.setCreatedAt(LocalDateTime.now());
        ruleMapper.insert(confidentialRule);

        // 5. 周末访问限制
        PermDynamicRule weekendRule = new PermDynamicRule();
        weekendRule.setRuleName("周末访问限制");
        weekendRule.setRuleCode("RULE_WEEKEND");
        weekendRule.setRuleType("TIME");
        weekendRule.setDimension("SPATIOTEMPORAL");
        weekendRule.setConditionExpression("#time.dayOfWeek == 6 or #time.dayOfWeek == 7");
        weekendRule.setActionType("DEGRADE");
        weekendRule.setPriority(110);
        weekendRule.setStatus(0); // 默认禁用
        weekendRule.setDescription("周末访问时降低权限级别");
        weekendRule.setCreatedAt(LocalDateTime.now());
        ruleMapper.insert(weekendRule);

        log.info(">>> 已创建 {} 条默认规则", 5);
    }
}

package com.example.demo.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.entity.SysUser;
import com.example.demo.permission.entity.*;
import com.example.demo.permission.mapper.*;
import com.example.demo.service.ISysUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ISysUserService userService;
    private final PermUserProfileMapper userProfileMapper;
    private final PermBehaviorEventMapper behaviorEventMapper;
    private final PermEntropyMonitorMapper entropyMonitorMapper;
    private final PermAuditSnapshotMapper auditSnapshotMapper;
    private final PermSandboxSimulationMapper sandboxSimulationMapper;
    private final PermDynamicRuleMapper dynamicRuleMapper;

    public DataInitializer(ISysUserService userService,
                          PermUserProfileMapper userProfileMapper,
                          PermBehaviorEventMapper behaviorEventMapper,
                          PermEntropyMonitorMapper entropyMonitorMapper,
                          PermAuditSnapshotMapper auditSnapshotMapper,
                          PermSandboxSimulationMapper sandboxSimulationMapper,
                          PermDynamicRuleMapper dynamicRuleMapper) {
        this.userService = userService;
        this.userProfileMapper = userProfileMapper;
        this.behaviorEventMapper = behaviorEventMapper;
        this.entropyMonitorMapper = entropyMonitorMapper;
        this.auditSnapshotMapper = auditSnapshotMapper;
        this.sandboxSimulationMapper = sandboxSimulationMapper;
        this.dynamicRuleMapper = dynamicRuleMapper;
    }

    @Override
    public void run(String... args) {
        initAdminUser();
        initUserProfiles();
        initBehaviorEvents();
        initEntropyMonitors();
        initAuditSnapshots();
        initSandboxSimulations();
        initDynamicRules();
        System.out.println(">>> 智能权限测试数据初始化完成");
    }

    private void initAdminUser() {
        SysUser existingUser = userService.getOne(
            new QueryWrapper<SysUser>().eq("user_name", "admin")
        );
        if (existingUser == null) {
            SysUser admin = new SysUser();
            admin.setUserName("admin");
            admin.setNickName("管理员");
            admin.setPassword("123456");
            admin.setEmail("admin@example.com");
            admin.setPhonenumber("15888888888");
            admin.setSex("0");
            admin.setAvatar("");
            admin.setStatus("0");
            admin.setCreateTime(LocalDateTime.now());
            userService.save(admin);
            System.out.println(">>> 默认管理员账号已创建: admin / 123456");
        }
    }

    private void initUserProfiles() {
        if (userProfileMapper.selectCount(null) == 0) {
            for (int i = 1; i <= 5; i++) {
                PermUserProfile profile = new PermUserProfile();
                profile.setUserId((long) i);
                profile.setTypicalAccessHours("[9,10,11,12,13,14,15,16,17,18]");
                profile.setTypicalLocations("[\"北京\", \"上海\"]");
                profile.setTypicalDevices("[\"PC-Chrome\", \"Mobile-Safari\"]");
                profile.setFrequentOperations("[\"view\", \"edit\", \"export\"]");
                profile.setSkillTags("[\"Java\", \"Python\", " + (i % 2 == 0 ? "\"AI\"" : "\"Database\"") + "]");
                profile.setExpertiseDomains("[\"技术\", \"管理\"]");
                profile.setPermissionUsageStats("{\"view\": 100, \"edit\": 50, \"delete\": 10}");
                profile.setBehaviorBaseline("{\"avgSessionTime\": 3600, \"avgOperations\": 150}");
                userProfileMapper.insert(profile);
            }
            System.out.println(">>> 用户画像数据已初始化");
        }
    }

    private void initBehaviorEvents() {
        if (behaviorEventMapper.selectCount(null) == 0) {
            String[] eventTypes = {"LOGIN", "LOGOUT", "ACCESS", "MODIFY", "EXPORT", "DELETE"};
            String[] resourceTypes = {"project", "customer", "order", "asset"};
            String[] operations = {"view", "edit", "delete", "export"};
            Random random = new Random();

            for (int i = 0; i < 100; i++) {
                PermBehaviorEvent event = new PermBehaviorEvent();
                event.setUserId((long) (random.nextInt(5) + 1));
                event.setEventType(eventTypes[random.nextInt(eventTypes.length)]);
                event.setEventSource("WEB");
                event.setResourceType(resourceTypes[random.nextInt(resourceTypes.length)]);
                event.setResourceId(String.valueOf(random.nextInt(100)));
                event.setOperation(operations[random.nextInt(operations.length)]);
                event.setEventData("{\"detail\": \"event_" + i + "\"}");
                event.setSessionId(UUID.randomUUID().toString());
                event.setIpAddress("192.168.1." + random.nextInt(255));
                event.setUserAgent("Mozilla/5.0 Chrome");
                event.setIsAnomaly(random.nextDouble() < 0.1 ? 1 : 0);
                event.setEventTime(LocalDateTime.now().minusHours(random.nextInt(168)));
                event.setProcessed(0);
                behaviorEventMapper.insert(event);
            }
            System.out.println(">>> 行为事件数据已初始化");
        }
    }

    private void initEntropyMonitors() {
        if (entropyMonitorMapper.selectCount(null) == 0) {
            Random random = new Random();
            for (int userId = 1; userId <= 5; userId++) {
                for (int day = 0; day < 30; day++) {
                    PermEntropyMonitor monitor = new PermEntropyMonitor();
                    monitor.setUserId((long) userId);
                    double entropy = 2.0 + random.nextDouble() * 2.0;
                    monitor.setPermissionEntropy(BigDecimal.valueOf(entropy));
                    int effective = 20 + random.nextInt(30);
                    int used = 10 + random.nextInt(effective - 10);
                    monitor.setEffectivePermissions(effective);
                    monitor.setUsedPermissions(used);
                    monitor.setUnusedPermissions(effective - used);
                    monitor.setDriftScore(BigDecimal.valueOf(random.nextDouble() * 100));
                    monitor.setCreepIndicators("[\"unused_role\", \"stale_permission\"]");
                    monitor.setRecommendedRevocations("[\"perm_" + random.nextInt(10) + "\"]");
                    monitor.setPeriodStart(LocalDateTime.now().minusDays(30 - day).withHour(0).withMinute(0));
                    monitor.setPeriodEnd(LocalDateTime.now().minusDays(30 - day).withHour(23).withMinute(59));
                    entropyMonitorMapper.insert(monitor);
                }
            }
            System.out.println(">>> 熵值监控数据已初始化");
        }
    }

    private void initAuditSnapshots() {
        if (auditSnapshotMapper.selectCount(null) == 0) {
            String[] changeTypes = {"GRANT", "REVOKE", "MODIFY", "INITIAL"};
            Random random = new Random();

            for (int userId = 1; userId <= 5; userId++) {
                for (int i = 0; i < 10; i++) {
                    PermAuditSnapshot snapshot = new PermAuditSnapshot();
                    snapshot.setUserId((long) userId);
                    snapshot.setSnapshotTime(LocalDateTime.now().minusDays(10 - i).withHour(random.nextInt(24)));
                    snapshot.setRoles("[\"admin\", \"user\", \"editor\"]");
                    snapshot.setPermissions("[\"*:*\", \"system:user:list\", \"system:role:list\"]");
                    snapshot.setEntityPermissions("[{\"type\": \"project\", \"id\": 1, \"perm\": \"own\"}]");
                    snapshot.setDataScope("1");
                    snapshot.setChangeType(changeTypes[random.nextInt(changeTypes.length)]);
                    snapshot.setChangeReason("系统自动记录 #" + i);
                    auditSnapshotMapper.insert(snapshot);
                }
            }
            System.out.println(">>> 审计快照数据已初始化");
        }
    }

    private void initSandboxSimulations() {
        if (sandboxSimulationMapper.selectCount(null) == 0) {
            String[] simTypes = {"WHAT_IF", "TIME_TRAVEL", "STRESS"};
            Random random = new Random();

            for (int i = 0; i < 20; i++) {
                PermSandboxSimulation simulation = new PermSandboxSimulation();
                simulation.setSimulationName("模拟测试 #" + (i + 1));
                simulation.setSimulationType(simTypes[random.nextInt(simTypes.length)]);
                simulation.setVariableOverrides("{\"userId\": 1, \"changeType\": \"addRole\"}");
                simulation.setAffectedUsers("[1, 2, 3, 4, 5]");
                simulation.setImpactHeatmap("{\"totalRequests\": " + (random.nextInt(1000) + 100) + "}");
                simulation.setConflictPredictions("[]");
                simulation.setExecutedBy(1L);
                simulation.setExecutionTime(LocalDateTime.now().minusDays(random.nextInt(30)));
                simulation.setExecutionDurationMs(random.nextInt(5000) + 100);
                sandboxSimulationMapper.insert(simulation);
            }
            System.out.println(">>> 沙箱模拟数据已初始化");
        }
    }

    private void initDynamicRules() {
        if (dynamicRuleMapper.selectCount(null) == 0) {
            List<PermDynamicRule> rules = Arrays.asList(
                createRule("工作时间访问控制", "RULE_WORK_TIME", "TIME", "SPATIOTEMPORAL",
                    "#spatiotemporal.isWorkingHours == true", "ALLOW", 1, "限制只能工作时间访问"),
                createRule("高风险操作审批", "RULE_HIGH_RISK", "RISK", "RISK",
                    "#risk.riskLevel > 3", "CONDITIONAL", 2, "高风险操作需要审批"),
                createRule("内部网络访问", "RULE_INTERNAL_IP", "NETWORK", "SPATIOTEMPORAL",
                    "#spatiotemporal.ipAddress.startsWith('192.168')", "ALLOW", 3, "允许内网访问"),
                createRule("敏感数据访问限制", "RULE_SENSITIVE_DATA", "DATA", "RISK",
                    "#risk.dataClassification < 3", "ALLOW", 4, "限制敏感数据访问"),
                createRule("异地登录检测", "RULE_UNUSUAL_LOCATION", "LOCATION", "BEHAVIORAL",
                    "#behavioral.isTypicalLocation == true", "ALLOW", 5, "检测异地登录")
            );
            rules.forEach(dynamicRuleMapper::insert);
            System.out.println(">>> 动态规则数据已初始化");
        }
    }

    private PermDynamicRule createRule(String name, String code, String type, String dimension,
                                       String condition, String action, int priority, String description) {
        PermDynamicRule rule = new PermDynamicRule();
        rule.setRuleName(name);
        rule.setRuleCode(code);
        rule.setRuleType(type);
        rule.setDimension(dimension);
        rule.setConditionExpression(condition);
        rule.setActionType(action);
        rule.setPriority(priority);
        rule.setStatus(1);
        rule.setDescription(description);
        return rule;
    }
}

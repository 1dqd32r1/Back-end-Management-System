-- 初始化数据库
CREATE DATABASE IF NOT EXISTS demo_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE demo_admin;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  user_id bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  user_name varchar(30) NOT NULL COMMENT '用户账号',
  nick_name varchar(30) NOT NULL COMMENT '用户昵称',
  password varchar(100) DEFAULT '' COMMENT '密码',
  email varchar(50) DEFAULT '' COMMENT '用户邮箱',
  phonenumber varchar(11) DEFAULT '' COMMENT '手机号码',
  sex char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  avatar varchar(255) DEFAULT '' COMMENT '头像地址',
  status char(1) DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (user_id)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
  role_id bigint(20) NOT NULL AUTO_INCREMENT,
  role_name varchar(30) NOT NULL COMMENT '角色名称',
  role_key varchar(100) NOT NULL COMMENT '角色权限字符串',
  role_sort int(4) NOT NULL COMMENT '显示顺序',
  data_scope char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly tinyint(1) DEFAULT '1' COMMENT '菜单树选择项关联显示',
  dept_check_strictly tinyint(1) DEFAULT '1' COMMENT '部门树选择项关联显示',
  status char(1) DEFAULT '0' COMMENT '状态（0：正常 1：停用）',
  del_flag char(1) DEFAULT '0' COMMENT '删除标志（0：代表存在 1：代表删除）',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (role_id)
);

-- 菜单权限表
CREATE TABLE IF NOT EXISTS sys_menu (
  menu_id bigint(20) NOT NULL AUTO_INCREMENT,
  menu_name varchar(50) NOT NULL COMMENT '菜单名称',
  parent_id bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  order_num int(4) DEFAULT '0' COMMENT '显示顺序',
  path varchar(200) DEFAULT '' COMMENT '路由地址',
  component varchar(255) DEFAULT NULL COMMENT '组件路径',
  menu_type char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  visible char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  is_refresh tinyint(1) DEFAULT '1' COMMENT '是否刷新',
  is_cache tinyint(1) DEFAULT '0' COMMENT '是否缓存',
  perms varchar(100) DEFAULT NULL COMMENT '权限标识',
  icon varchar(100) DEFAULT '#' COMMENT '菜单图标',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (menu_id)
);

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
  user_id bigint(20) NOT NULL,
  role_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
  role_id bigint(20) NOT NULL,
  menu_id bigint(20) NOT NULL,
  PRIMARY KEY (role_id, menu_id)
);

-- 部门表
CREATE TABLE IF NOT EXISTS sys_dept (
  dept_id bigint(20) NOT NULL AUTO_INCREMENT,
  parent_id bigint(20) DEFAULT '0' COMMENT '父部门ID',
  ancestors varchar(500) DEFAULT '' COMMENT '祖级列表',
  dept_name varchar(30) NOT NULL COMMENT '部门名称',
  order_num int(4) DEFAULT '0' COMMENT '显示顺序',
  leader varchar(20) DEFAULT NULL COMMENT '负责人',
  phone varchar(11) DEFAULT NULL COMMENT '联系电话',
  email varchar(50) DEFAULT NULL COMMENT '邮箱',
  status char(1) DEFAULT '0' COMMENT '部门状态（0：正常 1：停用）',
  del_flag char(1) DEFAULT '0' COMMENT '删除标志（0：代表存在 1：代表删除）',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (dept_id)
);

-- 岗位表
CREATE TABLE IF NOT EXISTS sys_post (
  post_id bigint(20) NOT NULL AUTO_INCREMENT,
  post_code varchar(64) NOT NULL COMMENT '岗位编码',
  post_name varchar(50) NOT NULL COMMENT '岗位名称',
  post_sort int(4) DEFAULT '0' COMMENT '显示顺序',
  status char(1) DEFAULT '0' COMMENT '状态（0：正常 1：停用）',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (post_id)
);

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS sys_user_post (
  user_id bigint(20) NOT NULL,
  post_id bigint(20) NOT NULL,
  PRIMARY KEY (user_id, post_id)
);

-- 实体表（项目、客户、订单、资产）
CREATE TABLE IF NOT EXISTS sys_entity (
  entity_id bigint(20) NOT NULL AUTO_INCREMENT,
  entity_type varchar(50) NOT NULL COMMENT '实体类型（project,customer,order,asset）',
  entity_id_ref bigint(20) NOT NULL COMMENT '实体ID关联',
  entity_name varchar(255) NOT NULL COMMENT '实体名称',
  create_time datetime DEFAULT NULL,
  update_time datetime DEFAULT NULL,
  PRIMARY KEY (entity_id),
  KEY idx_entity_type (entity_type)
);

-- 用户实体权限关联表
CREATE TABLE IF NOT EXISTS sys_user_entity (
  user_id bigint(20) NOT NULL,
  entity_id bigint(20) NOT NULL,
  entity_type varchar(50) NOT NULL,
  permission_type varchar(50) COMMENT '权限类型（view,edit,delete,own）',
  PRIMARY KEY (user_id, entity_id, entity_type)
);

-- 插入角色数据
INSERT INTO sys_role VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', NOW(), NOW());
INSERT INTO sys_role VALUES (2, '普通用户', 'user', 2, '2', 1, 1, '0', '0', NOW(), NOW());

-- 插入部门数据
INSERT INTO sys_dept VALUES (1, 0, '0', '技术部', 1, '张三', '13800138000', 'tech@company.com', '0', '0', NOW(), NOW());
INSERT INTO sys_dept VALUES (2, 1, '0,1', '前端组', 1, '李四', '13900139000', 'frontend@company.com', '0', '0', NOW(), NOW());
INSERT INTO sys_dept VALUES (3, 1, '0,1', '后端组', 2, '王五', '13700137000', 'backend@company.com', '0', '0', NOW(), NOW());

-- 插入岗位数据
INSERT INTO sys_post VALUES (1, 'ceo', '首席执行官', 1, '0', NOW(), NOW());
INSERT INTO sys_post VALUES (2, 'architect', '架构师', 2, '0', NOW(), NOW());
INSERT INTO sys_post VALUES (3, 'developer', '开发工程师', 3, '0', NOW(), NOW());
INSERT INTO sys_post VALUES (4, 'tester', '测试工程师', 4, '0', NOW(), NOW());

-- 插入用户数据（密码为明文 123456）
INSERT IGNORE INTO sys_user (user_id, user_name, nick_name, password, email, phonenumber, sex, avatar, status, create_time) VALUES
(1, 'admin', '管理员', '123456', 'admin@example.com', '15888888888', '0', '', '0', NOW());
INSERT IGNORE INTO sys_user (user_id, user_name, nick_name, password, email, phonenumber, sex, avatar, status, create_time) VALUES
(2, 'test', '测试用户', '123456', 'test@example.com', '15888888889', '0', '', '0', NOW());

-- 插入用户角色关联
INSERT INTO sys_user_role VALUES (1, 1); -- admin-超级管理员
INSERT INTO sys_user_role VALUES (2, 2); -- test-普通用户

-- 插入实体数据（示例）
INSERT INTO sys_entity VALUES (1, 'project', 1, '智能权限系统项目', NOW(), NOW());
INSERT INTO sys_entity VALUES (2, 'customer', 1, '客户A公司', NOW(), NOW());
INSERT INTO sys_entity VALUES (3, 'order', 1001, '订单-001', NOW(), NOW());
INSERT INTO sys_entity VALUES (4, 'asset', 5001, '服务器-01', NOW(), NOW());

-- 插入用户实体权限
INSERT INTO sys_user_entity VALUES (1, 1, 'project', 'own'); -- admin 拥有项目所有权
INSERT INTO sys_user_entity VALUES (1, 2, 'customer', 'edit'); -- admin 可编辑客户信息
INSERT INTO sys_user_entity VALUES (2, 3, 'order', 'view'); -- test 只能查看订单

-- =====================================================
-- 智能权限中枢系统表结构
-- =====================================================

-- 1. 权限上下文快照表
CREATE TABLE IF NOT EXISTS perm_context_snapshot (
    snapshot_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '快照ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    session_id VARCHAR(64) NOT NULL COMMENT '会话ID',

    -- 组织维度
    dept_id BIGINT COMMENT '当前部门ID',
    post_ids VARCHAR(500) COMMENT '岗位ID列表(JSON)',
    virtual_team_ids VARCHAR(500) COMMENT '虚拟团队ID列表(JSON)',

    -- 实体维度
    active_entity_type VARCHAR(50) COMMENT '当前操作实体类型',
    active_entity_id BIGINT COMMENT '当前操作实体ID',

    -- 时空维度
    ip_address VARCHAR(64) COMMENT 'IP地址',
    geo_location VARCHAR(100) COMMENT '地理位置(经纬度)',
    device_fingerprint VARCHAR(128) COMMENT '设备指纹',
    access_time_range VARCHAR(50) COMMENT '访问时间段',
    access_frequency INT DEFAULT 0 COMMENT '访问频率(次/小时)',

    -- 风险维度
    operation_sensitivity TINYINT DEFAULT 0 COMMENT '操作敏感度(0-10)',
    data_classification TINYINT DEFAULT 0 COMMENT '数据分级(0-4)',
    anomaly_score DECIMAL(5,2) DEFAULT 0 COMMENT '异常评分(0-100)',
    risk_level TINYINT DEFAULT 0 COMMENT '风险等级(0-4)',

    -- 决策结果
    decision_result VARCHAR(20) COMMENT '决策结果(ALLOW/DENY/CONDITIONAL)',
    decision_latency_ms INT COMMENT '决策延迟(毫秒)',
    decision_factors JSON COMMENT '决策因子(JSON)',

    -- 元数据
    request_id VARCHAR(64) COMMENT '请求追踪ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_time (user_id, created_at),
    INDEX idx_session (session_id),
    INDEX idx_risk (anomaly_score, risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限上下文快照表';

-- 2. 动态权限规则表
CREATE TABLE IF NOT EXISTS perm_dynamic_rule (
    rule_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(50) NOT NULL UNIQUE COMMENT '规则编码',
    rule_type VARCHAR(20) NOT NULL COMMENT '规则类型(TIME/GEO/BEHAVIOR/RISK/CUSTOM)',

    -- 规则定义
    dimension VARCHAR(20) NOT NULL COMMENT '作用维度',
    condition_expression TEXT NOT NULL COMMENT '条件表达式(SpEL)',
    action_type VARCHAR(20) NOT NULL COMMENT '动作类型(ALLOW/DENY/ELEVATE/DEGRADE)',
    priority INT DEFAULT 100 COMMENT '优先级(越小越高)',

    -- 生效配置
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态(0停用 1启用)',

    -- 元数据
    description VARCHAR(500) COMMENT '规则描述',
    created_by BIGINT COMMENT '创建人',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_type_status (rule_type, status),
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态权限规则表';

-- 3. 权限决策日志表
CREATE TABLE IF NOT EXISTS perm_decision_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    request_id VARCHAR(64) NOT NULL COMMENT '请求追踪ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    resource_id VARCHAR(100) COMMENT '资源ID',
    operation VARCHAR(50) NOT NULL COMMENT '操作类型',

    -- 决策过程
    snapshot_id BIGINT COMMENT '上下文快照ID',
    matched_rules JSON COMMENT '匹配的规则列表',
    conflict_resolution JSON COMMENT '冲突消解记录',

    -- 结果
    final_decision VARCHAR(20) NOT NULL COMMENT '最终决策',
    denial_reason VARCHAR(500) COMMENT '拒绝原因',
    explanation TEXT COMMENT '可解释性说明',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_time (user_id, created_at),
    INDEX idx_resource (resource_type, resource_id),
    INDEX idx_decision (final_decision)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限决策日志表';

-- 4. 用户行为画像表
CREATE TABLE IF NOT EXISTS perm_user_profile (
    profile_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '画像ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',

    -- 行为模式
    typical_access_hours VARCHAR(100) COMMENT '典型访问时段(JSON数组)',
    typical_locations VARCHAR(500) COMMENT '典型位置列表(JSON)',
    typical_devices VARCHAR(500) COMMENT '典型设备列表(JSON)',
    frequent_operations JSON COMMENT '频繁操作统计',
    collaboration_patterns JSON COMMENT '协作模式图谱',

    -- 技能图谱
    skill_tags JSON COMMENT '技能标签',
    expertise_domains JSON COMMENT '专业领域',

    -- 权限使用统计
    permission_usage_stats JSON COMMENT '权限使用统计',
    unused_permissions JSON COMMENT '未使用权限',
    overprivileged_score DECIMAL(5,2) COMMENT '过度授权评分',

    -- 行为基线
    behavior_baseline JSON COMMENT '行为基线模型',
    last_baseline_update DATETIME COMMENT '基线最后更新时间',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为画像表';

-- 5. 行为事件流表
CREATE TABLE IF NOT EXISTS perm_behavior_event (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事件ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    event_type VARCHAR(50) NOT NULL COMMENT '事件类型',
    event_source VARCHAR(50) COMMENT '事件来源(WEB/API/SDK)',

    -- 事件详情
    resource_type VARCHAR(50) COMMENT '资源类型',
    resource_id VARCHAR(100) COMMENT '资源ID',
    operation VARCHAR(50) COMMENT '操作类型',
    event_data JSON COMMENT '事件详细数据',

    -- 上下文
    session_id VARCHAR(64) COMMENT '会话ID',
    ip_address VARCHAR(64) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT 'User-Agent',
    geo_location VARCHAR(100) COMMENT '地理位置',

    -- 分析标记
    is_anomaly TINYINT DEFAULT 0 COMMENT '是否异常',
    anomaly_type VARCHAR(50) COMMENT '异常类型',
    processed TINYINT DEFAULT 0 COMMENT '是否已处理',

    event_time DATETIME NOT NULL COMMENT '事件时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_time (user_id, event_time),
    INDEX idx_type (event_type),
    INDEX idx_anomaly (is_anomaly, anomaly_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='行为事件流表';

-- 6. 权限熵值监控表
CREATE TABLE IF NOT EXISTS perm_entropy_monitor (
    monitor_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '监控ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',

    -- 熵值计算
    permission_entropy DECIMAL(10,4) COMMENT '权限熵值',
    effective_permissions INT COMMENT '有效权限数',
    used_permissions INT COMMENT '已使用权限数',
    unused_permissions INT COMMENT '未使用权限数',

    -- 权限漂移检测
    drift_score DECIMAL(5,2) COMMENT '权限漂移评分',
    creep_indicators JSON COMMENT '权限膨胀指标',
    recommended_revocations JSON COMMENT '建议回收的权限',

    -- 统计周期
    period_start DATETIME NOT NULL COMMENT '统计周期开始',
    period_end DATETIME NOT NULL COMMENT '统计周期结束',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_period (user_id, period_start),
    INDEX idx_drift (drift_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限熵值监控表';

-- 7. 权限沙盒模拟记录表
CREATE TABLE IF NOT EXISTS perm_sandbox_simulation (
    simulation_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模拟ID',
    simulation_name VARCHAR(100) NOT NULL COMMENT '模拟名称',
    simulation_type VARCHAR(20) NOT NULL COMMENT '模拟类型(WHAT_IF/TIME_TRAVEL/STRESS)',

    -- 模拟配置
    base_snapshot_id BIGINT COMMENT '基础快照ID',
    target_timestamp DATETIME COMMENT '目标时间点(时光机)',
    variable_overrides JSON COMMENT '变量覆盖配置',

    -- 模拟结果
    affected_users JSON COMMENT '受影响用户列表',
    impact_heatmap JSON COMMENT '影响面热力图数据',
    conflict_predictions JSON COMMENT '冲突预测',

    -- 执行信息
    executed_by BIGINT COMMENT '执行人',
    execution_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    execution_duration_ms INT COMMENT '执行耗时',

    INDEX idx_type_time (simulation_type, execution_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限沙盒模拟记录表';

-- 8. 权限审计快照表(时光机)
CREATE TABLE IF NOT EXISTS perm_audit_snapshot (
    audit_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '审计ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    snapshot_time DATETIME NOT NULL COMMENT '快照时间点',

    -- 权限状态快照
    roles JSON NOT NULL COMMENT '角色列表快照',
    permissions JSON NOT NULL COMMENT '权限列表快照',
    entity_permissions JSON COMMENT '实体权限快照',
    data_scope VARCHAR(500) COMMENT '数据范围快照',

    -- 关联变更
    change_event_id BIGINT COMMENT '触发变更的事件ID',
    change_type VARCHAR(50) COMMENT '变更类型',
    change_reason VARCHAR(500) COMMENT '变更原因',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_user_time (user_id, snapshot_time),
    INDEX idx_change (change_event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限审计快照表';

-- 9. 联邦服务注册表
CREATE TABLE IF NOT EXISTS perm_federation_service (
    service_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务ID',
    service_name VARCHAR(100) NOT NULL UNIQUE COMMENT '服务名称',
    service_code VARCHAR(50) NOT NULL UNIQUE COMMENT '服务编码',
    service_url VARCHAR(255) NOT NULL COMMENT '服务地址',

    -- 同步配置
    sync_mode VARCHAR(20) DEFAULT 'EVENTUAL' COMMENT '同步模式(EVENTUAL/STRONG)',
    sync_interval INT DEFAULT 5000 COMMENT '同步间隔(毫秒)',
    last_sync_time DATETIME COMMENT '最后同步时间',
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态',

    -- CRDT状态
    crdt_clock VARCHAR(100) COMMENT '向量时钟',
    crdt_state_hash VARCHAR(64) COMMENT '状态哈希',

    status TINYINT DEFAULT 1 COMMENT '服务状态(0离线 1在线)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联邦服务注册表';

-- 10. 权限变更事件表(CRDT同步)
CREATE TABLE IF NOT EXISTS perm_change_event (
    event_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '事件ID',
    event_type VARCHAR(50) NOT NULL COMMENT '事件类型',
    event_source VARCHAR(50) NOT NULL COMMENT '事件来源服务',

    -- 变更内容
    target_user_id BIGINT COMMENT '目标用户ID',
    target_role_id BIGINT COMMENT '目标角色ID',
    change_data JSON NOT NULL COMMENT '变更数据',

    -- CRDT元数据
    vector_clock VARCHAR(200) NOT NULL COMMENT '向量时钟',
    operation_id VARCHAR(64) NOT NULL UNIQUE COMMENT '操作ID(幂等)',
    tombstone TINYINT DEFAULT 0 COMMENT '墓碑标记',

    -- 同步状态
    synced_services JSON COMMENT '已同步服务列表',
    sync_status VARCHAR(20) DEFAULT 'PENDING' COMMENT '同步状态',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_target (target_user_id, target_role_id),
    INDEX idx_sync (sync_status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限变更事件表';

-- 11. 虚拟团队表
CREATE TABLE IF NOT EXISTS sys_virtual_team (
    team_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '团队ID',
    team_name VARCHAR(100) NOT NULL COMMENT '团队名称',
    team_code VARCHAR(50) NOT NULL UNIQUE COMMENT '团队编码',
    team_type VARCHAR(50) COMMENT '团队类型(PROJECT/CROSS_DEPT/TASK)',

    -- 关联信息
    project_id BIGINT COMMENT '关联项目ID',
    parent_dept_id BIGINT COMMENT '归属部门ID',

    -- 权限配置
    inherited_permissions JSON COMMENT '继承的权限',
    exclusive_permissions JSON COMMENT '独有权限',

    -- 成员管理
    leader_id BIGINT COMMENT '负责人ID',
    member_count INT DEFAULT 0 COMMENT '成员数量',

    -- 生效配置
    effective_start DATETIME COMMENT '生效开始时间',
    effective_end DATETIME COMMENT '生效结束时间',
    status TINYINT DEFAULT 1 COMMENT '状态',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_type_status (team_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟团队表';

-- 12. 虚拟团队成员关联表
CREATE TABLE IF NOT EXISTS sys_virtual_team_member (
    team_id BIGINT NOT NULL COMMENT '团队ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    member_role VARCHAR(50) DEFAULT 'MEMBER' COMMENT '成员角色(LEADER/MEMBER/GUEST)',
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',

    PRIMARY KEY (team_id, user_id),
    INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='虚拟团队成员关联表';

-- 13. 数据分级配置表
CREATE TABLE IF NOT EXISTS perm_data_classification (
    class_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分级ID',
    class_name VARCHAR(50) NOT NULL COMMENT '分级名称',
    class_level TINYINT NOT NULL UNIQUE COMMENT '分级级别(0-4)',
    class_code VARCHAR(20) NOT NULL UNIQUE COMMENT '分级编码(PUBLIC/INTERNAL/CONFIDENTIAL/SECRET/TOP_SECRET)',

    -- 访问规则
    min_role_level INT COMMENT '最低角色级别要求',
    required_approvals INT DEFAULT 0 COMMENT '审批人数要求',
    access_time_restriction VARCHAR(100) COMMENT '访问时间限制',

    description VARCHAR(500) COMMENT '分级描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_level (class_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据分级配置表';

-- 插入默认数据分级配置
INSERT INTO perm_data_classification (class_name, class_level, class_code, min_role_level, required_approvals, description) VALUES
('公开', 0, 'PUBLIC', 0, 0, '可公开访问的数据'),
('内部', 1, 'INTERNAL', 1, 0, '仅内部人员可访问'),
('机密', 2, 'CONFIDENTIAL', 2, 1, '需审批访问的机密数据'),
('秘密', 3, 'SECRET', 3, 2, '高度敏感数据，需多人审批'),
('绝密', 4, 'TOP_SECRET', 4, 3, '最高级别机密数据');

-- =====================================================
-- 智能权限系统示例数据
-- =====================================================

-- 插入动态权限规则示例
INSERT INTO perm_dynamic_rule (rule_name, rule_code, rule_type, dimension, condition_expression, action_type, priority, status, description) VALUES
('工作时间访问限制', 'TIME_WORKING_HOURS', 'TIME', 'SPATIOTEMPORAL', '#time.currentHour >= 9 && #time.currentHour <= 18', 'ALLOW', 10, 1, '仅允许在工作时间(9:00-18:00)访问'),
('非工作时间高风险操作', 'TIME_NON_WORKING', 'TIME', 'SPATIOTEMPORAL', '#time.currentHour < 9 || #time.currentHour > 18', 'CONDITIONAL', 20, 1, '非工作时间访问需要额外审批'),
('办公网络访问', 'GEO_OFFICE_NETWORK', 'GEO', 'SPATIOTEMPORAL', '#geo.ipAddress.startsWith(''192.168.'') || #geo.ipAddress.startsWith(''10.'')', 'ALLOW', 15, 1, '允许办公网络IP访问'),
('境外IP限制', 'GEO_FOREIGN_BLOCK', 'GEO', 'SPATIOTEMPORAL', '!#geo.isInCountry(''CN'')', 'DENY', 5, 1, '禁止境外IP访问敏感资源'),
('高频率访问检测', 'BEHAVIOR_HIGH_FREQ', 'BEHAVIOR', 'BEHAVIORAL', '#behavior.accessFrequency > 100', 'CONDITIONAL', 30, 1, '高频访问触发二次验证'),
('异常设备检测', 'BEHAVIOR_UNUSUAL_DEVICE', 'BEHAVIOR', 'BEHAVIORAL', '!#behavior.isTypicalDevice', 'CONDITIONAL', 25, 1, '非常用设备访问需要验证'),
('高风险操作限制', 'RISK_HIGH_RISK', 'RISK', 'RISK', '#risk.riskLevel >= 3', 'DENY', 1, 1, '风险等级>=3时拒绝访问'),
('机密数据访问', 'RISK_CONFIDENTIAL', 'RISK', 'RISK', '#risk.dataClassification >= 2 && #risk.trustScore < 80', 'CONDITIONAL', 3, 1, '访问机密数据需要高信任评分');

-- 插入虚拟团队示例
INSERT INTO sys_virtual_team (team_name, team_code, team_type, project_id, leader_id, member_count, inherited_permissions, exclusive_permissions, effective_start, status) VALUES
('智能权限项目组', 'TEAM_PERM_001', 'PROJECT', 1, 1, 3, '["user:view","role:view","menu:view"]', '["permission:admin","rule:manage"]', NOW(), 1),
('跨部门协作组', 'TEAM_CROSS_001', 'CROSS_DEPT', NULL, 2, 2, '["project:view","customer:view"]', '["project:edit"]', NOW(), 1),
('临时任务组', 'TEAM_TASK_001', 'TASK', NULL, 1, 4, '["task:view"]', '["task:edit","task:assign"]', NOW(), 1);

-- 插入虚拟团队成员
INSERT INTO sys_virtual_team_member (team_id, user_id, member_role) VALUES
(1, 1, 'LEADER'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER'),
(2, 2, 'LEADER'),
(2, 3, 'MEMBER'),
(3, 1, 'LEADER'),
(3, 2, 'MEMBER'),
(3, 3, 'MEMBER'),
(3, 4, 'GUEST');

-- 插入用户行为画像示例
INSERT INTO perm_user_profile (user_id, typical_access_hours, typical_locations, typical_devices, frequent_operations, skill_tags, expertise_domains, permission_usage_stats, behavior_baseline) VALUES
(1, '[9,10,11,14,15,16,17]', '["192.168.1.0/24", "北京"]', '["Chrome-Windows", "Safari-iOS"]', '{"view": 150, "edit": 45, "delete": 5}', '["系统管理", "权限配置"]', '["IT管理", "安全合规"]', '{"total_permissions": 50, "used_permissions": 35, "unused_permissions": 15}', '{"avg_daily_ops": 50, "typical_hours": [9,10,11,14,15,16,17]}'),
(2, '[9,10,14,15,16]', '["192.168.1.0/24"]', '["Chrome-Windows"]', '{"view": 80, "edit": 20}', '["前端开发"]', '["Web开发"]', '{"total_permissions": 30, "used_permissions": 20, "unused_permissions": 10}', '{"avg_daily_ops": 25, "typical_hours": [9,10,14,15,16]}');

-- 插入权限熵值监控示例
INSERT INTO perm_entropy_monitor (user_id, permission_entropy, effective_permissions, used_permissions, unused_permissions, drift_score, creep_indicators, recommended_revocations, period_start, period_end) VALUES
(1, 2.3456, 50, 35, 15, 23.5, '{"unused_ratio": 0.3, "last_used_days": 30}', '["system:advanced", "data:export"]', DATE_SUB(NOW(), INTERVAL 30 DAY), NOW()),
(2, 1.8765, 30, 20, 10, 15.2, '{"unused_ratio": 0.33, "last_used_days": 15}', '["report:generate"]', DATE_SUB(NOW(), INTERVAL 30 DAY), NOW());

-- 插入审计快照示例
INSERT INTO perm_audit_snapshot (user_id, snapshot_time, roles, permissions, entity_permissions, data_scope, change_type, change_reason) VALUES
(1, DATE_SUB(NOW(), INTERVAL 7 DAY), '["admin", "user"]', '["*:*"]', '[{"type": "project", "id": 1, "perm": "own"}]', '1', 'INITIAL', '系统初始化'),
(1, NOW(), '["admin", "user"]', '["*:*"]', '[{"type": "project", "id": 1, "perm": "own"}, {"type": "customer", "id": 1, "perm": "edit"}]', '1', 'GRANT', '授权客户编辑权限'),
(2, DATE_SUB(NOW(), INTERVAL 5 DAY), '["user"]', '["user:view", "role:view", "project:view"]', '[{"type": "order", "id": 1001, "perm": "view"}]', '3', 'INITIAL', '角色分配'),
(2, NOW(), '["user", "project_member"]', '["user:view", "role:view", "project:view", "project:edit"]', '[{"type": "order", "id": 1001, "perm": "view"}, {"type": "project", "id": 1, "perm": "edit"}]', '3', 'GRANT', '加入项目组');

-- 插入行为事件示例
INSERT INTO perm_behavior_event (user_id, event_type, event_source, resource_type, resource_id, operation, event_data, session_id, ip_address, user_agent, is_anomaly, event_time) VALUES
(1, 'LOGIN', 'WEB', NULL, NULL, 'login', '{"method": "password"}', 'sess_001', '192.168.1.100', 'Mozilla/5.0 Chrome/120.0', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 'PERMISSION_CHECK', 'API', 'project', '1', 'view', '{"result": "ALLOW"}', 'sess_001', '192.168.1.100', 'Mozilla/5.0 Chrome/120.0', 0, DATE_SUB(NOW(), INTERVAL 50 MINUTE)),
(1, 'OPERATION', 'WEB', 'project', '1', 'edit', '{"fields": ["name", "status"]}', 'sess_001', '192.168.1.100', 'Mozilla/5.0 Chrome/120.0', 0, DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
(2, 'LOGIN', 'WEB', NULL, NULL, 'login', '{"method": "password"}', 'sess_002', '192.168.1.101', 'Mozilla/5.0 Chrome/120.0', 0, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 'PERMISSION_CHECK', 'API', 'order', '1001', 'view', '{"result": "ALLOW"}', 'sess_002', '192.168.1.101', 'Mozilla/5.0 Chrome/120.0', 0, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
(1, 'LOGIN', 'WEB', NULL, NULL, 'login', '{"method": "password"}', 'sess_003', '10.0.0.50', 'Mozilla/5.0 Safari/iOS', 0, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
(1, 'PERMISSION_CHECK', 'API', 'customer', '1', 'edit', '{"result": "CONDITIONAL", "reason": "非工作时间需要审批"}', 'sess_003', '10.0.0.50', 'Mozilla/5.0 Safari/iOS', 1, DATE_SUB(NOW(), INTERVAL 25 MINUTE));

-- 插入联邦服务示例
INSERT INTO perm_federation_service (service_name, service_code, service_url, sync_mode, sync_interval, sync_status, status) VALUES
('订单服务', 'ORDER_SVC', 'http://order-service:8080', 'EVENTUAL', 5000, 'SYNCED', 1),
('客户服务', 'CUSTOMER_SVC', 'http://customer-service:8080', 'EVENTUAL', 5000, 'SYNCED', 1),
('报表服务', 'REPORT_SVC', 'http://report-service:8080', 'EVENTUAL', 10000, 'PENDING', 1);

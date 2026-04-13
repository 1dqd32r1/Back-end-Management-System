# 智能权限管理系统

基于 Spring Boot + Vue3 + Element Plus 的智能权限管理系统，实现了完整的 RBAC 权限控制与智能权限决策功能。

## 项目简介

本系统是一个企业级权限管理解决方案，集成了传统 RBAC 权限模型与智能权限决策引擎，支持多维度上下文感知、动态规则配置、权限熵值监控等高级功能。

## 技术栈

### 后端
- **Spring Boot 3.4.0** - 基础框架
- **Spring Security 6** - 安全认证
- **MyBatis-Plus 3.5.5** - ORM 框架
- **MySQL 8.0** - 数据库
- **JWT** - 无状态认证

### 前端
- **Vue 3** - 前端框架
- **Element Plus** - UI 组件库
- **ECharts** - 图表可视化
- **Axios** - HTTP 请求
- **Pinia** - 状态管理
- **Vue Router** - 路由管理

## 功能模块

### 基础权限管理
- 用户管理
- 角色管理
- 菜单权限管理
- 部门管理
- 岗位管理

### 智能权限中枢
1. **虚拟团队管理** - 项目团队、跨部门团队、临时任务组的动态管理
2. **动态规则引擎** - 基于 SpEL 的灵活权限规则配置
3. **行为分析画像** - 用户行为追踪与异常检测
4. **熵值监控** - 权限分散度与漂移监控
5. **沙箱模拟** - What-If 模拟、压力测试
6. **审计时光机** - 权限变更追溯与回放
7. **权限决策** - 多维度上下文感知决策

## 项目结构

```
demo/
├── sql/                    # 数据库脚本
│   ├── schema.sql         # 表结构与初始化数据
│   └── init.sql           # 用户初始化脚本
├── src/                    # 后端源码
│   └── main/
│       ├── java/
│       │   └── com/example/demo/
│       │       ├── config/           # 配置类
│       │       ├── controller/       # 控制器
│       │       ├── entity/           # 实体类
│       │       ├── mapper/           # MyBatis Mapper
│       │       ├── permission/       # 智能权限模块
│       │       │   ├── annotation/   # 注解定义
│       │       │   ├── aop/          # AOP 切面
│       │       │   ├── config/       # 权限配置
│       │       │   ├── context/      # 上下文模型
│       │       │   ├── controller/   # 权限控制器
│       │       │   ├── dimension/    # 维度处理器
│       │       │   ├── entity/       # 权限实体
│       │       │   ├── mapper/       # 权限 Mapper
│       │       │   ├── sandbox/      # 沙箱模拟
│       │       │   └── service/      # 权限服务
│       │       ├── security/         # 安全模块
│       │       └── service/          # 业务服务
│       └── resources/
│           └── application.yml       # 应用配置
├── ui/                     # 前端源码
│   ├── src/
│   │   ├── api/            # API 接口
│   │   ├── components/     # 公共组件
│   │   ├── layout/         # 布局组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── utils/          # 工具函数
│   │   └── views/          # 页面视图
│   ├── package.json
│   └── vite.config.ts
├── build.gradle            # Gradle 构建配置
└── README.md
```

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+

### 数据库初始化
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE demo_admin DEFAULT CHARACTER SET utf8mb4"

# 导入表结构
mysql -u root -p demo_admin < sql/schema.sql
```

### 后端启动
```bash
# 进入项目目录
cd demo

# 启动后端
./gradlew bootRun
```

### 前端启动
```bash
# 进入前端目录
cd ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 访问系统
- 前端地址：http://localhost:5173
- 后端地址：http://localhost:8080
- 默认账号：admin / 123456

## 核心功能说明

### 智能权限决策流程

```
用户请求 → 上下文采集 → 多维度评估 → 规则匹配 → 决策输出
                ↓
        ┌──────────────────────────────────┐
        │  组织维度：部门、岗位、汇报线      │
        │  时空维度：IP、时间、设备          │
        │  风险维度：敏感度、异常评分        │
        │  行为维度：习惯时段、常用设备      │
        └──────────────────────────────────┘
```

### 动态规则示例
```java
// 工作时间访问控制
#spatiotemporal.isWorkingHours == true

// 高风险操作审批
#risk.riskLevel > 3

// 内网访问限制
#spatiotemporal.ipAddress.startsWith('192.168')
```

## 配置说明

### 数据库配置 (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_admin
    username: root
    password: your_password
```

### JWT 配置
系统使用固定的 JWT 密钥（生产环境建议从配置文件读取）：
- Token 有效期：24 小时

## 许可证

MIT License

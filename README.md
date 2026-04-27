# 智能权限管理系统

基于 Spring Boot 3 + Vue 3 + Element Plus 的企业级智能权限管理系统，集成 RBAC 权限控制、智能权限决策引擎、3D数据可视化等先进功能。

## 功能特性

### 核心功能
- 🔐 **用户认证与授权** - JWT无状态认证，Spring Security安全框架
- 👥 **用户管理** - 用户增删改查、头像上传、个人信息维护
- 🎭 **角色管理** - 角色权限分配、菜单权限控制
- 📢 **通知公告** - 系统公告发布与管理

### 智能权限中枢
- 🏢 **虚拟团队管理** - 项目团队、跨部门团队、临时任务组的动态管理
- ⚙️ **动态规则引擎** - 基于SpEL的灵活权限规则配置
- 📊 **行为分析画像** - 用户行为追踪与异常检测
- 📈 **熵值监控** - 权限分散度与漂移监控
- 🧪 **沙箱模拟** - What-If模拟、压力测试
- ⏰ **审计时光机** - 权限变更追溯与回放
- 🎯 **权限决策** - 多维度上下文感知决策

### 数据可视化
- 🏙️ **3D数据城市** - 将枯燥报表数据转化为3D城市景观
  - 销售额映射为建筑高度
  - 用户增长映射为城市面积
  - 异常数据以红色警示塔显示
  - 支持鼠标漫游、点击下钻查看详情
- 📉 **服务监控** - CPU、内存、磁盘实时监控
- 📊 **数据图表** - ECharts可视化图表展示

### 开发者工具
- 📖 **Swagger接口文档** - 在线API文档，支持在线调试
- ⌨️ **命令面板** - Ctrl+K 快捷键快速导航与操作

## 快捷操作

### 命令面板 (Ctrl+K / Cmd+K)
按下 `Ctrl+K`（Windows）或 `Cmd+K`（Mac）打开命令面板，支持：
- **快速导航** - 输入"去 首页"、"用户管理"等快速跳转页面
- **搜索关键词** - 支持中文、拼音首字母模糊搜索
- **快捷操作** - 切换主题、刷新页面、退出登录等
- **最近使用** - 自动记录最近5条命令

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.4.0 | 基础框架 |
| Spring Security | 6.x | 安全认证框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| JWT | 0.11.5 | Token认证 |
| SpringDoc | 2.3.0 | API文档 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 前端框架 |
| Element Plus | Latest | UI组件库 |
| Three.js | Latest | 3D可视化 |
| ECharts | Latest | 图表库 |
| Pinia | Latest | 状态管理 |
| Vue Router | Latest | 路由管理 |
| Axios | Latest | HTTP请求 |

## 项目结构

```
demo/
├── sql/                        # 数据库脚本
│   ├── schema.sql             # 表结构与初始化数据
│   └── init.sql               # 用户初始化脚本
├── src/main/java/com/example/demo/
│   ├── config/                # 配置类
│   │   ├── SecurityConfig.java        # 安全配置
│   │   ├── SwaggerConfig.java         # Swagger配置
│   │   └── MybatisPlusConfig.java     # MyBatis配置
│   ├── controller/            # 控制器
│   │   ├── SysUserController.java     # 用户管理
│   │   ├── MonitorController.java     # 系统监控
│   │   ├── DataCityController.java    # 3D数据城市
│   │   └── CommonController.java      # 通用接口
│   ├── entity/                # 实体类
│   ├── mapper/                # MyBatis Mapper
│   ├── service/               # 业务服务
│   ├── security/              # 安全模块
│   │   ├── JwtAuthenticationTokenFilter.java
│   │   ├── LoginUser.java
│   │   └── UserDetailsServiceImpl.java
│   └── permission/            # 智能权限模块
│       ├── annotation/        # 注解定义
│       ├── aop/               # AOP切面
│       ├── context/           # 上下文模型
│       ├── dimension/         # 维度处理器
│       ├── entity/            # 权限实体
│       ├── mapper/            # 权限Mapper
│       ├── sandbox/           # 沙箱模拟
│       └── service/           # 权限服务
├── src/main/resources/
│   └── application.yml        # 应用配置
├── ui/                        # 前端源码
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 公共组件
│   │   │   └── CommandPalette.vue  # 命令面板组件
│   │   ├── composables/       # 组合式函数
│   │   │   └── useCommandRegistry.ts  # 命令注册中心
│   │   ├── commands/          # 命令配置
│   │   │   └── index.ts       # 命令注册模块
│   │   ├── layout/            # 布局组件
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面视图
│   │       ├── dashboard/     # 首页仪表盘
│   │       ├── system/        # 系统管理
│   │       │   ├── user/      # 用户管理
│   │       │   ├── role/      # 角色管理
│   │       │   ├── notice/    # 通知公告
│   │       │   └── swagger/   # 接口文档
│   │       ├── monitor/       # 系统监控
│   │       ├── permission/    # 智能权限
│   │       ├── datacity/      # 3D数据城市
│   │       └── ai/            # AI对话
│   ├── package.json
│   └── vite.config.ts
├── build.gradle               # Gradle构建配置
└── README.md
```

## 快速开始

### 环境要求
- JDK 21+
- Node.js 18+
- MySQL 8.0+
- Gradle 8.x

### 1. 克隆项目
```bash
# GitHub
git clone https://github.com/1dqd32r1/Back-end-Management-System.git

# Gitee
git clone https://gitee.com/Zhang-loveMoney/backend-management-system.git

cd Back-end-Management-System
```

### 2. 数据库初始化
```bash
# 创建数据库
mysql -u root -p -e "CREATE DATABASE demo_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci"

# 导入表结构
mysql -u root -p demo_admin < sql/schema.sql
```

### 3. 配置数据库连接
修改 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_admin?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: your_password
```

### 4. 启动后端
```bash
# Windows
./gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

### 5. 启动前端
```bash
# 进入前端目录
cd ui

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 6. 访问系统
- **前端地址**：http://localhost:5173
- **后端地址**：http://localhost:8080
- **Swagger文档**：http://localhost:8080/swagger-ui/index.html
- **默认账号**：admin / 123456

### 注意事项
1. **前端依赖安装** - 如遇到依赖冲突，请使用 `npm install --legacy-peer-deps`
2. **数据库初始化** - 首次启动时会自动创建管理员账号
3. **端口占用** - 确保 8080 和 5173 端口未被占用

## 系统截图

### 3D数据城市
将销售额、用户增长等数据以3D城市建筑形式呈现，支持鼠标漫游、点击下钻查看详情。

### 系统监控
实时监控服务器CPU、内存、磁盘使用情况。

### 权限决策
多维度上下文感知的智能权限决策引擎。

## API文档

启动后端后访问 Swagger UI：
- 地址：http://localhost:8080/swagger-ui/index.html
- 支持在线调试API
- 需要认证的接口请先点击"Authorize"输入Token

## 智能权限决策流程

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

### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/demo_admin?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8
    username: root
    password: 123456
```

### 文件上传配置
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
```

### MyBatis-Plus配置
```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  global-config:
    db-config:
      id-type: auto
  configuration:
    map-underscore-to-camel-case: true
```

## 仓库地址

- **GitHub**: https://github.com/1dqd32r1/Back-end-Management-System
- **Gitee**: https://gitee.com/Zhang-loveMoney/backend-management-system

## 许可证

[MIT License](LICENSE)

## 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

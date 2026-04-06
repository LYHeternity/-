# 一站式创业者综合服务平台

一个完整的前后端分离的创业服务平台，支持创业者、投资人、服务商和管理员四类角色，提供项目展示、投资对接、资源服务、内容管理等核心功能。

## 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [系统架构](#系统架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [目录结构](#目录结构)
- [API接口文档](#api接口文档)
- [数据库设计](#数据库设计)
- [常见问题](#常见问题)
- [开发规范](#开发规范)

## 项目概述

本项目是一个综合性的创业服务平台，旨在为创业者、投资人和服务商提供一站式的资源对接和交流服务。平台主要包含以下核心功能：

- **项目管理**：创业者可以发布、管理和展示创业项目
- **投资对接**：投资人可以浏览项目、表达投资意向
- **服务市场**：服务商可以提供各类创业服务
- **内容社区**：发布创业资讯、经验分享
- **消息系统**：支持用户间交流和系统通知
- **后台管理**：管理员可以进行用户管理、内容审核、数据统计

## 技术栈

### 后端技术
- **框架**：Spring Boot 2.7.x
- **持久层**：MyBatis Plus 3.5.x
- **安全框架**：Spring Security + JWT
- **数据库**：MySQL 8.0
- **缓存**：Redis 6.x（可选）
- **构建工具**：Maven 3.6+
- **Java版本**：JDK 1.8

### 前端技术
- **框架**：Vue.js 2.6.x
- **路由**：Vue Router 3.x
- **状态管理**：Vuex 3.x
- **UI组件库**：Element UI 2.x
- **HTTP客户端**：Axios
- **构建工具**：Vue CLI 4.x
- **Node.js版本**：14.x+

### 开发工具
- **版本控制**：Git
- **代码编辑器**：IntelliJ IDEA / VS Code
- **数据库工具**：Navicat / DBeaver

## 功能模块

### 1. 用户系统
- 用户注册/登录（支持用户名、手机、邮箱）
- 角色选择（创业者、投资人、服务商）
- 个人信息管理
- 头像上传
- 密码修改
- 用户权限管理

### 2. 项目管理
- 项目发布与编辑
- 项目列表展示（支持筛选、搜索、排序）
- 项目详情页
- 项目收藏
- 项目点赞
- 项目进度更新
- 团队成员管理
- 项目审核流程

### 3. 投资对接
- 投资意向表达
- 投资意向审核
- 投资人信息展示
- 项目对接记录

### 4. 服务市场
- 服务发布与编辑
- 服务分类浏览
- 服务详情页
- 服务收藏
- 服务评价
- 服务订单管理
- 服务审核流程

### 5. 内容社区
- 文章发布
- 文章列表
- 文章详情
- 评论功能
- 点赞功能
- 标签管理

### 6. 消息系统
- 即时消息
- 消息历史记录
- 系统通知
- 消息未读提醒

### 7. 后台管理
- **用户管理**：用户列表、用户状态管理、角色分配
- **项目管理**：项目列表、项目审核、项目编辑、项目删除
- **服务管理**：服务列表、服务审核、服务编辑、服务删除
- **内容审核**：待审核项目、待审核服务、审核通过/拒绝
- **数据统计**：用户统计、项目统计、服务统计、趋势分析
- **系统设置**：系统配置、参数管理
- **操作日志**：系统操作记录
- **数据备份**：数据库备份功能

## 系统架构

### 整体架构
```
┌─────────────────────────────────────────────────────────┐
│                        前端层                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │  用户页面 │  │  管理后台 │  │  公共页面 │            │
│  └──────────┘  └──────────┘  └──────────┘            │
└─────────────────────────────────────────────────────────┘
                            ↓ HTTP/HTTPS
┌─────────────────────────────────────────────────────────┐
│                      网关/代理层                         │
│              Nginx / Vue CLI DevServer                  │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────┐
│                       后端层                             │
│  ┌─────────────────────────────────────────────────┐   │
│  │              Spring Boot Application            │   │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  │   │
│  │  │Controller│  │  Service  │  │  Mapper  │  │   │
│  │  └──────────┘  └──────────┘  └──────────┘  │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────┐    ┌─────────────────┐
│     MySQL       │    │      Redis      │
│   (主数据库)    │    │    (缓存)       │
└─────────────────┘    └─────────────────┘
```

### 分层架构
- **表现层**：Controller层，处理HTTP请求和响应
- **业务层**：Service层，实现核心业务逻辑
- **持久层**：Mapper/DAO层，负责数据库操作
- **实体层**：Entity层，定义数据模型

## 环境要求

### 开发环境
- **操作系统**：Windows 10/11、macOS 10.14+、Linux（Ubuntu 18.04+）
- **JDK**：1.8 或更高版本
- **Maven**：3.6.0 或更高版本
- **Node.js**：14.0 或更高版本
- **npm**：6.0 或更高版本
- **MySQL**：8.0 或更高版本
- **Redis**：6.0 或更高版本（可选）

### 生产环境建议
- **服务器**：4核8G以上配置
- **操作系统**：CentOS 7+ 或 Ubuntu 18.04+
- **数据库**：MySQL 8.0 主从复制
- **缓存**：Redis 集群
- **Web服务器**：Nginx 1.18+

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd lunwen
```

### 2. 数据库初始化

#### 方式一：命令行导入

```bash
# 使用 root 登录 MySQL
mysql -u root -p

# 创建数据库
source database/create_database.sql;

# 使用数据库
use entrepreneur_platform;

# 创建表
source database/create_table.sql;

# 初始化数据
source database/init_data.sql;
```

#### 方式二：使用MySQL客户端工具

1. 打开MySQL客户端（Navicat、DBeaver等）
2. 连接到MySQL服务器
3. 依次执行以下SQL文件：
   - `database/create_database.sql` - 创建数据库
   - `database/create_table.sql` - 创建数据表
   - `database/init_data.sql` - 初始化测试数据

### 3. 后端启动

#### 配置数据库连接

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/entrepreneur_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 修改为你的MySQL密码
```

#### 启动后端服务

```bash
cd backend

# 使用Maven启动
mvn spring-boot:run

# 或先打包再运行
mvn clean package
java -jar target/entrepreneur-platform-1.0.0.jar
```

后端服务默认运行在：`http://localhost:8080`

#### 默认账号

系统启动后会自动创建以下账号：

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | Admin@1234 | 系统管理员 |
| 创业者 | founder1 | Admin@1234 | 测试创业者账号 |
| 投资人 | investor1 | Admin@1234 | 测试投资人账号 |
| 服务商 | provider1 | Admin@1234 | 测试服务商账号 |

### 4. 前端启动

#### 安装依赖

```bash
cd frontend
npm install
```

如果网络较慢，可以使用淘宝镜像：

```bash
npm install --registry=https://registry.npmmirror.com
```

#### 启动开发服务器

```bash
npm run dev
```

前端服务默认运行在：`http://localhost:8081`

#### 访问系统

打开浏览器访问：`http://localhost:8081`

使用管理员账号登录：
- 用户名：`admin`
- 密码：`Admin@1234`

### 5. 生产环境部署

#### 后端部署

```bash
# 打包
cd backend
mvn clean package -DskipTests

# 上传JAR包到服务器
scp target/entrepreneur-platform-1.0.0.jar user@server:/opt/app/

# 在服务器上运行
ssh user@server
cd /opt/app
nohup java -jar entrepreneur-platform-1.0.0.jar > app.log 2>&1 &
```

#### 前端部署

```bash
# 构建
cd frontend
npm run build

# 上传dist目录到服务器
scp -r dist/* user@server:/var/www/html/

# 配置Nginx
```

Nginx配置示例：

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 配置说明

### 后端配置（application.yml）

```yaml
server:
  port: 8080                    # 后端服务端口
  servlet:
    context-path: /api          # 上下文路径

spring:
  application:
    name: entrepreneur-platform  # 应用名称
  
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/entrepreneur_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
  
  redis:
    host: localhost
    port: 6379
    database: 0
    timeout: 3000
    # 如无Redis，可注释掉或设置enabled: false

  servlet:
    multipart:
      max-file-size: 50MB        # 最大上传文件大小
      max-request-size: 50MB

mybatis-plus:
  mapper-locations: classpath:mapper/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

jwt:
  secret: your-jwt-secret-key-here  # JWT密钥，生产环境请修改
  expire-hours: 24                    # Token过期时间（小时）

file:
  upload-dir: ./uploads              # 文件上传目录

cors:
  allowed-origins: http://localhost:8081  # 允许的跨域来源
```

### 前端配置（vue.config.js）

```javascript
module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  productionSourceMap: false
}
```

## 目录结构

```
lunwen/
├── backend/                    # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/entrepreneur/platform/
│   │   │   │       ├── controller/       # 控制器层
│   │   │   │       │   ├── admin/        # 管理员控制器
│   │   │   │       │   └── ...           # 其他控制器
│   │   │   │       ├── service/          # 服务层
│   │   │   │       ├── mapper/           # 数据访问层
│   │   │   │       ├── entity/           # 实体类
│   │   │   │       ├── dto/              # 数据传输对象
│   │   │   │       ├── common/           # 公共类
│   │   │   │       └── config/           # 配置类
│   │   │   └── resources/
│   │   │       ├── mapper/               # MyBatis XML映射文件
│   │   │       └── application.yml       # 应用配置文件
│   │   └── test/                          # 测试代码
│   └── pom.xml                            # Maven配置文件
│
├── frontend/                   # Vue.js 前端项目
│   ├── src/
│   │   ├── api/                # API接口定义
│   │   ├── assets/             # 静态资源
│   │   ├── components/         # 公共组件
│   │   ├── views/              # 页面组件
│   │   │   ├── admin/          # 管理员页面
│   │   │   ├── project/        # 项目相关页面
│   │   │   ├── resource/       # 服务相关页面
│   │   │   └── ...             # 其他页面
│   │   ├── router/             # 路由配置
│   │   ├── store/              # Vuex状态管理
│   │   ├── utils/              # 工具函数
│   │   ├── App.vue              # 根组件
│   │   └── main.js              # 入口文件
│   ├── public/                 # 公共静态文件
│   ├── package.json             # npm依赖配置
│   └── vue.config.js            # Vue CLI配置
│
├── database/                   # 数据库相关
│   ├── create_database.sql     # 创建数据库脚本
│   ├── create_table.sql        # 创建数据表脚本
│   └── init_data.sql           # 初始化数据脚本
│
└── README.md                   # 项目说明文档
```

## API接口文档

### 认证接口

#### 用户登录
- **接口**：`POST /api/auth/login`
- **请求体**：
```json
{
  "account": "admin",
  "password": "Admin@1234"
}
```
- **响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": 1,
      "username": "admin",
      "role": "admin"
    }
  }
}
```

#### 用户注册
- **接口**：`POST /api/auth/register`
- **请求体**：
```json
{
  "username": "newuser",
  "password": "Password@123",
  "email": "user@example.com",
  "phone": "13800138000",
  "realName": "张三",
  "role": "entrepreneur"
}
```

### 项目接口

#### 获取项目列表
- **接口**：`GET /api/project/list`
- **参数**：
  - `current`：页码（默认1）
  - `size`：每页数量（默认10）
  - `industry`：行业筛选（可选）
  - `stage`：阶段筛选（可选）
  - `orderBy`：排序方式（hot/view/默认按时间）

#### 发布项目
- **接口**：`POST /api/project/publish`
- **请求头**：`Authorization: Bearer <token>`
- **请求体**：
```json
{
  "title": "AI智能助手项目",
  "description": "这是一个AI智能助手项目...",
  "industry": "人工智能",
  "stage": "seed",
  "fundingNeeded": 500,
  "equityOffered": 10
}
```

### 服务接口

#### 获取服务列表
- **接口**：`GET /api/resource/list`
- **参数**：
  - `current`：页码
  - `size`：每页数量
  - `category`：分类筛选
  - `type`：类型筛选

#### 发布服务
- **接口**：`POST /api/resource/publish`
- **请求头**：`Authorization: Bearer <token>`

### 管理员接口

所有管理员接口需要管理员权限，请求头需携带token。

#### 获取项目列表（管理员）
- **接口**：`GET /api/admin/project/list`

#### 项目审核
- **接口**：`PUT /api/admin/audit/project/{id}`
- **请求体**：
```json
{
  "status": "online",
  "rejectReason": ""
}
```

#### 获取用户列表
- **接口**：`GET /api/admin/user/list`

## 数据库设计

### 核心数据表

#### user（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR | 用户名 |
| password | VARCHAR | 密码（加密） |
| real_name | VARCHAR | 真实姓名 |
| email | VARCHAR | 邮箱 |
| phone | VARCHAR | 手机号 |
| role | VARCHAR | 角色 |
| status | INT | 状态 |
| avatar | VARCHAR | 头像 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

#### project（项目表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR | 项目名称 |
| description | TEXT | 项目描述 |
| founder_id | BIGINT | 创始人ID |
| industry | VARCHAR | 行业 |
| stage | VARCHAR | 阶段 |
| funding_needed | DECIMAL | 融资需求 |
| equity_offered | DECIMAL | 出让股权 |
| status | VARCHAR | 状态 |
| view_count | INT | 浏览量 |
| like_count | INT | 点赞数 |
| create_time | DATETIME | 创建时间 |

#### resource（服务表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR | 服务名称 |
| description | TEXT | 服务描述 |
| provider_id | BIGINT | 服务商ID |
| category | VARCHAR | 分类 |
| price | DECIMAL | 价格 |
| status | VARCHAR | 状态 |
| view_count | INT | 浏览量 |
| favorite_count | INT | 收藏数 |
| create_time | DATETIME | 创建时间 |

更多数据表结构详见 `database/create_table.sql`

## 常见问题

### 1. 数据库连接失败
**问题**：启动后端时提示数据库连接错误

**解决方案**：
- 确认MySQL服务已启动
- 确认数据库 `entrepreneur_platform` 已创建
- 检查 `application.yml` 中的数据库用户名和密码是否正确
- 确认MySQL版本是否为8.0+

### 2. 前端401未授权
**问题**：访问接口时返回401错误

**解决方案**：
- 确认已登录并获取token
- 检查token是否过期（默认24小时）
- 确认后端服务已正常启动
- 检查跨域配置是否正确

### 3. Redis连接失败
**问题**：启动时提示Redis连接错误

**解决方案**：
- 如果未安装Redis，可在 `application.yml` 中注释Redis配置
- 或安装并启动Redis服务
- 检查Redis地址和端口配置

### 4. 图片上传失败
**问题**：上传图片时提示错误

**解决方案**：
- 检查 `application.yml` 中的 `file.upload-dir` 配置
- 确认上传目录有写入权限
- 检查文件大小是否超过限制（默认50MB）

### 5. 前端页面空白
**问题**：访问前端时页面空白

**解决方案**：
- 打开浏览器开发者工具查看Console错误
- 确认后端服务已启动
- 检查API代理配置是否正确
- 确认 `npm install` 已成功执行

### 6. 提交人显示未知
**问题**：项目管理或服务管理中的提交人显示"未知"

**解决方案**：
- 确认数据库中用户数据完整
- 确认项目/服务的创始人ID/提供商ID有效
- 重启后端服务

## 开发规范

### 代码规范

#### Java代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 接口返回统一使用 `Result<T>` 包装
- Service层添加 `@Transactional` 注解
- 合理使用日志（SLF4J）

#### Vue代码规范
- 组件命名使用PascalCase
- 文件名使用PascalCase
- Props定义使用camelCase
- 事件名使用kebab-case
- 合理使用Vuex管理状态
- API调用统一放在 `src/api` 目录

### Git提交规范

```
<type>(<scope>): <subject>

类型说明：
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具相关

示例：
feat(auth): 添加用户注册功能
fix(project): 修复项目列表分页问题
docs: 更新README文档
```

### 分支管理

- `main`：主分支，用于生产环境
- `develop`：开发分支
- `feature/*`：功能分支
- `bugfix/*`：bug修复分支
- `hotfix/*`：紧急修复分支

## 许可证

MIT License

## 联系方式

如有问题或建议，欢迎反馈。

---

**注意**：本项目仅供学习和参考使用，请勿用于商业用途。

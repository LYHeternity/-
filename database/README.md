# 数据库说明

## 1. 创建数据库

在 MySQL 中执行（或使用 root 登录后）：

```sql
CREATE DATABASE IF NOT EXISTS entrepreneur_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 2. 执行脚本顺序

1. **建表**：执行 `create_table.sql`
2. **初始化数据**：执行 `init_data.sql`

## 3. 默认账号（由后端启动时自动创建）

- **管理员**：用户名 `admin`，密码 `Admin@1234`（首次启动时自动创建并分配全部权限）

## 4. 测试账号（来自 init_data.sql，密码需与管理员一致时由后端同步）

执行完 init_data 后，测试用户为：

- 创业者：`founder1` 
- 投资人：`investor1`
- 服务商：`provider1`

若无法登录，请使用管理员登录后在「用户管理」中重置密码为 `Admin@1234`。

## 5. 连接配置

后端 `application.yml` 中默认：

- 地址：`localhost:3306`
- 数据库：`entrepreneur_platform`
- 用户名：`root`
- 密码：`root`

请根据本机 MySQL 修改。

# 微服务项目架构

## 项目结构
- **gateway-service**: 网关模块，端口 8080
  - Token 认证过滤器
  - 路由转发
- **auth-service**: 认证服务模块，端口 8082
  - 用户登录
  - 用户注册
  - 密码加密（BCrypt）
- **user-service**: 用户服务模块，端口 8081
  - 用户数据管理
  - 提供内部接口供 Feign 调用
- **common-module**: 公共模块
  - 统一返回结果
  - 全局异常处理
  - JWT 工具类
  - 密码加密配置
  - 常量定义
- **feign-api**: Feign 调用模块
  - 服务间调用接口定义
  - DTO 数据传输对象

## 技术栈
- JDK 17
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Spring Cloud Alibaba 2023.0.3.2
- Nacos 注册中心
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT (jjwt 0.11.5)
- BCrypt 密码加密
- SLF4J + Logback 日志框架

## 启动步骤
1. 启动 Nacos (默认 localhost:8848)
2. 启动 MySQL 数据库并执行初始化脚本
3. 启动 user-service (端口 8081)
4. 启动 auth-service (端口 8082)
5. 启动 gateway-service (端口 8080)

## 数据库初始化
```sql
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（加密）',
  `nickname` VARCHAR(50) COMMENT '昵称',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `deleted` INT DEFAULT 0 COMMENT '删除标记：0未删除 1已删除',
  `status` INT DEFAULT 1 COMMENT '用户状态：1正常 0禁用',
  `create_time` DATETIME COMMENT '创建时间',
  `update_time` DATETIME COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

## 接口文档

### 认证接口（通过网关访问）

#### 用户注册
- 接口: POST http://localhost:8080/auth/register
- 请求体:
```json
{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com",
  "phone": "13800138000"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": "注册成功"
}
```

#### 用户登录
- 接口: POST http://localhost:8080/auth/login
- 请求体:
```json
{
  "username": "testuser",
  "password": "123456"
}
```
- 响应:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      "email": "test@example.com",
      "phone": "13800138000",
      "status": 1,
      "deleted": 0
    }
  }
}
```

### 访问需要认证的接口
在请求头中添加 Token：
```
Authorization: Bearer <token>
```

## 架构特点

### 1. 微服务架构
- 服务拆分：认证服务、用户服务独立部署
- 服务注册与发现：Nacos
- 服务间调用：OpenFeign

### 2. 安全机制
- 密码加密：BCrypt 不可逆加密
- Token 认证：JWT 无状态认证
- 网关鉴权：统一 Token 验证
- 白名单机制：登录、注册接口无需认证

### 3. 统一处理
- 全局异常处理：统一异常返回格式
- 参数校验：Bean Validation
- 日志记录：SLF4J + Logback
- 统一返回格式：Result<T>

### 4. 用户状态管理
- deleted: 逻辑删除标记
- status: 用户启用/禁用状态
- 登录时自动校验用户状态

## 日志文件
- gateway-service: logs/gateway-service.log
- auth-service: logs/auth-service.log
- user-service: logs/user-service.log

## 测试流程
1. 注册用户
2. 登录获取 Token
3. 携带 Token 访问其他接口

# 微服务项目架构

## 项目结构
- **gateway-service**: 网关模块，端口 8080
- **user-service**: 用户服务模块，端口 8081
- **common-module**: 公共模块
- **feign-api**: Feign 调用模块

## 技术栈
- JDK 17
- Spring Boot 3.4.1
- Spring Cloud 2024.0.0
- Spring Cloud Alibaba 2023.0.3.2
- Nacos 注册中心

## 启动步骤
1. 启动 Nacos (默认 localhost:8848)
2. 启动 user-service
3. 启动 gateway-service

## 测试接口
- 直接访问: http://localhost:8081/test/hello
- 通过网关: http://localhost:8080/user/test/hello

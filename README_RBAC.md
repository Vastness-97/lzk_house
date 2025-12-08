# RBAC权限管理功能文档

## 功能概述
基于角色的访问控制（RBAC）系统，实现用户-角色-权限的三层权限管理模型。

## 数据库设计

### 1. 角色表 (role)
```sql
CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) COMMENT '角色描述',
  `status` INT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
);
```

### 2. 权限表 (permission)
```sql
CREATE TABLE `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `resource_type` VARCHAR(20) COMMENT '资源类型：menu菜单 button按钮 api接口',
  `url` VARCHAR(200) COMMENT '资源URL',
  `method` VARCHAR(10) COMMENT '请求方法：GET POST PUT DELETE',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
  `status` INT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
);
```

### 3. 用户角色关联表 (user_role)
```sql
CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
);
```

### 4. 角色权限关联表 (role_permission)
```sql
CREATE TABLE `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
);
```

## 初始化数据

### 初始角色
- **超级管理员** (ROLE_ADMIN): 拥有所有权限
- **普通用户** (ROLE_USER): 基础查询权限

### 初始权限
- 用户管理: user:manage, user:query, user:add, user:update, user:delete
- 角色管理: role:manage, role:query, role:add, role:update, role:delete

## API接口

### 角色管理接口

#### 1. 查询所有角色
```
GET http://localhost:8080/role/list
Headers: Authorization: Bearer <token>
```

#### 2. 查询角色详情
```
GET http://localhost:8080/role/{id}
Headers: Authorization: Bearer <token>
```

#### 3. 添加角色
```
POST http://localhost:8080/role/add
Headers: Authorization: Bearer <token>
Body:
{
  "roleName": "测试角色",
  "roleCode": "ROLE_TEST",
  "description": "测试角色描述",
  "status": 1
}
```

#### 4. 更新角色
```
PUT http://localhost:8080/role/update
Headers: Authorization: Bearer <token>
Body:
{
  "id": 1,
  "roleName": "更新角色名",
  "roleCode": "ROLE_TEST",
  "description": "更新描述",
  "status": 1
}
```

#### 5. 删除角色
```
DELETE http://localhost:8080/role/delete/{id}
Headers: Authorization: Bearer <token>
```

#### 6. 查询用户的角色
```
GET http://localhost:8080/role/user/{userId}
Headers: Authorization: Bearer <token>
```

#### 7. 给用户分配角色
```
POST http://localhost:8080/role/assign/{userId}
Headers: Authorization: Bearer <token>
Body: [1, 2]  // 角色ID列表
```

#### 8. 给角色分配权限
```
POST http://localhost:8080/role/permission/{roleId}
Headers: Authorization: Bearer <token>
Body: [1, 2, 3]  // 权限ID列表
```

### 权限管理接口

#### 1. 查询所有权限
```
GET http://localhost:8080/permission/list
Headers: Authorization: Bearer <token>
```

#### 2. 查询权限详情
```
GET http://localhost:8080/permission/{id}
Headers: Authorization: Bearer <token>
```

#### 3. 添加权限
```
POST http://localhost:8080/permission/add
Headers: Authorization: Bearer <token>
Body:
{
  "permissionName": "测试权限",
  "permissionCode": "test:query",
  "resourceType": "api",
  "url": "/test/**",
  "method": "GET",
  "parentId": 0,
  "status": 1
}
```

#### 4. 更新权限
```
PUT http://localhost:8080/permission/update
Headers: Authorization: Bearer <token>
Body:
{
  "id": 1,
  "permissionName": "更新权限",
  "permissionCode": "test:query",
  "status": 1
}
```

#### 5. 删除权限
```
DELETE http://localhost:8080/permission/delete/{id}
Headers: Authorization: Bearer <token>
```

#### 6. 查询用户的权限
```
GET http://localhost:8080/permission/user/{userId}
Headers: Authorization: Bearer <token>
```

## 权限验证机制

### 1. JWT Token包含权限
登录时，系统会查询用户的所有权限并写入JWT Token中：
```java
String token = JwtUtil.generateToken(userId, username, permissions);
```

### 2. 网关层权限验证
网关的 PermissionFilter 会验证请求权限：
- 白名单路径直接放行（登录、注册）
- 从Token中提取权限列表
- 根据请求路径和方法匹配权限
- 超级管理员（ROLE_ADMIN）拥有所有权限

### 3. 权限编码规则
- 格式：`资源:操作`
- 示例：
  - `user:query` - 用户查询权限
  - `user:add` - 用户新增权限
  - `role:manage` - 角色管理权限（包含所有操作）
  - `*:*` - 所有权限

### 4. HTTP方法与权限映射
- GET → query（查询）
- POST → add（新增）
- PUT → update（更新）
- DELETE → delete（删除）
- manage → 所有操作

## 使用流程

### 1. 初始化数据库
```bash
# 执行SQL脚本
mysql -u root -p lzk_house < user-service/src/main/resources/sql/rbac.sql
```

### 2. 启动服务
按照原有启动顺序启动所有服务

### 3. 测试流程

#### 步骤1: 使用admin账号登录
```bash
POST http://localhost:8080/auth/login
{
  "username": "admin",
  "password": "123456"
}
```
返回的token中包含所有权限

#### 步骤2: 创建新角色
```bash
POST http://localhost:8080/role/add
Headers: Authorization: Bearer <admin_token>
{
  "roleName": "测试角色",
  "roleCode": "ROLE_TEST",
  "description": "测试角色",
  "status": 1
}
```

#### 步骤3: 给角色分配权限
```bash
POST http://localhost:8080/role/permission/3
Headers: Authorization: Bearer <admin_token>
Body: [2]  // 只分配user:query权限
```

#### 步骤4: 注册新用户
```bash
POST http://localhost:8080/auth/register
{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户"
}
```

#### 步骤5: 给新用户分配角色
```bash
POST http://localhost:8080/role/assign/2
Headers: Authorization: Bearer <admin_token>
Body: [3]  // 分配测试角色
```

#### 步骤6: 新用户登录
```bash
POST http://localhost:8080/auth/login
{
  "username": "testuser",
  "password": "123456"
}
```
返回的token中只包含user:query权限

#### 步骤7: 测试权限
```bash
# 有权限的操作 - 成功
GET http://localhost:8080/user/info
Headers: Authorization: Bearer <testuser_token>

# 无权限的操作 - 返回403
POST http://localhost:8080/user/add
Headers: Authorization: Bearer <testuser_token>
```

## 核心代码说明

### 1. JWT工具类增强
```java
// 生成包含权限的token
public static String generateToken(Long userId, String username, List<String> permissions)

// 从token中获取权限
public static List<String> getPermissions(String token)
```

### 2. 权限过滤器
- 位置: gateway-service/filter/PermissionFilter.java
- 优先级: -99（在认证过滤器之后）
- 功能: 验证用户是否有访问接口的权限

### 3. Feign接口
- PermissionFeignClient: 用于auth-service调用user-service获取用户权限

### 4. 权限注解（预留）
- @RequirePermission: 可用于方法级权限控制

## 注意事项

1. **权限更新**: 修改用户权限后，需要重新登录才能生效（因为权限存储在JWT中）
2. **超级管理员**: ROLE_ADMIN角色拥有所有权限，无需单独分配
3. **白名单**: 登录、注册接口在白名单中，无需权限验证
4. **权限粒度**: 当前实现为接口级权限控制，可根据需要扩展到按钮级、数据级
5. **性能优化**: 权限信息存储在JWT中，避免每次请求都查询数据库

## 扩展建议

1. **动态权限**: 实现权限热更新，无需重新登录
2. **数据权限**: 实现行级数据权限控制
3. **权限缓存**: 使用Redis缓存权限信息
4. **审计日志**: 记录权限变更和访问日志
5. **权限树**: 实现权限的树形结构展示

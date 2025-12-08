-- RBAC权限管理表结构

USE lzk_house;

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `description` VARCHAR(200) COMMENT '角色描述',
  `status` INT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` VARCHAR(50) NOT NULL COMMENT '权限名称',
  `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
  `resource_type` VARCHAR(20) COMMENT '资源类型：menu菜单 button按钮 api接口',
  `url` VARCHAR(200) COMMENT '资源URL',
  `method` VARCHAR(10) COMMENT '请求方法：GET POST PUT DELETE',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
  `status` INT DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` BIGINT NOT NULL COMMENT '角色ID',
  `permission_id` BIGINT NOT NULL COMMENT '权限ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 插入初始角色数据
INSERT INTO `role` (`role_name`, `role_code`, `description`, `status`) VALUES
('超级管理员', 'ROLE_ADMIN', '系统超级管理员，拥有所有权限', 1),
('普通用户', 'ROLE_USER', '普通用户，基础权限', 1);

-- 插入初始权限数据
INSERT INTO `permission` (`permission_name`, `permission_code`, `resource_type`, `url`, `method`, `parent_id`, `status`) VALUES
('用户管理', 'user:manage', 'menu', '/user/**', NULL, 0, 1),
('用户查询', 'user:query', 'api', '/user/info', 'GET', 1, 1),
('用户新增', 'user:add', 'api', '/user/add', 'POST', 1, 1),
('用户修改', 'user:update', 'api', '/user/update', 'PUT', 1, 1),
('用户删除', 'user:delete', 'api', '/user/delete', 'DELETE', 1, 1),
('角色管理', 'role:manage', 'menu', '/role/**', NULL, 0, 1),
('角色查询', 'role:query', 'api', '/role/list', 'GET', 6, 1),
('角色新增', 'role:add', 'api', '/role/add', 'POST', 6, 1),
('角色修改', 'role:update', 'api', '/role/update', 'PUT', 6, 1),
('角色删除', 'role:delete', 'api', '/role/delete', 'DELETE', 6, 1);

-- 给超级管理员分配所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;

-- 给普通用户分配基础权限
INSERT INTO `role_permission` (`role_id`, `permission_id`) VALUES (2, 2);

-- 给admin用户分配超级管理员角色
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES (1, 1);

package com.lzk.user.service;

import com.lzk.user.entity.Permission;
import com.lzk.user.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 权限服务
 * 处理权限管理相关业务逻辑
 */
@Service
public class PermissionService {

    private static final Logger log = LoggerFactory.getLogger(PermissionService.class);
    
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /**
     * 查询所有权限
     */
    public List<Permission> listPermissions() {
        return permissionMapper.selectList(null);
    }

    /**
     * 根据ID查询权限
     */
    public Permission getPermissionById(Long id) {
        return permissionMapper.selectById(id);
    }

    /**
     * 添加权限
     */
    public void addPermission(Permission permission) {
        log.info("添加权限: permissionName={}, permissionCode={}", 
                permission.getPermissionName(), permission.getPermissionCode());
        permissionMapper.insert(permission);
    }

    /**
     * 更新权限
     */
    public void updatePermission(Permission permission) {
        log.info("更新权限: id={}", permission.getId());
        permissionMapper.updateById(permission);
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long id) {
        log.info("删除权限: id={}", id);
        permissionMapper.deleteById(id);
    }

    /**
     * 查询用户的所有权限
     */
    public List<Permission> getPermissionsByUserId(Long userId) {
        log.debug("查询用户权限: userId={}", userId);
        return permissionMapper.selectPermissionsByUserId(userId);
    }
}

package com.lzk.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lzk.user.entity.Role;
import com.lzk.user.entity.RolePermission;
import com.lzk.user.entity.UserRole;
import com.lzk.user.mapper.RoleMapper;
import com.lzk.user.mapper.RolePermissionMapper;
import com.lzk.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 角色服务
 * 处理角色管理相关业务逻辑
 */
@Service
public class RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RolePermissionMapper rolePermissionMapper;

    public RoleService(RoleMapper roleMapper, UserRoleMapper userRoleMapper, RolePermissionMapper rolePermissionMapper) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
        this.rolePermissionMapper = rolePermissionMapper;
    }

    /**
     * 查询所有角色
     */
    public List<Role> listRoles() {
        return roleMapper.selectList(null);
    }

    /**
     * 根据ID查询角色
     */
    public Role getRoleById(Long id) {
        return roleMapper.selectById(id);
    }

    /**
     * 添加角色
     */
    public void addRole(Role role) {
        log.info("添加角色: roleName={}", role.getRoleName());
        roleMapper.insert(role);
    }

    /**
     * 更新角色
     */
    public void updateRole(Role role) {
        log.info("更新角色: id={}", role.getId());
        roleMapper.updateById(role);
    }

    /**
     * 删除角色
     */
    public void deleteRole(Long id) {
        log.info("删除角色: id={}", id);
        roleMapper.deleteById(id);
    }

    /**
     * 查询用户的所有角色
     */
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectRolesByUserId(userId);
    }

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    @Transactional
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        log.info("为用户分配角色: userId={}, roleIds={}", userId, roleIds);
        // 先删除用户原有角色
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        roleIds.forEach(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        });
        log.info("用户角色分配完成: userId={}", userId);
    }

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        log.info("为角色分配权限: roleId={}, permissionIds={}", roleId, permissionIds);
        // 先删除角色原有权限
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        permissionIds.forEach(permissionId -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            rolePermissionMapper.insert(rolePermission);
        });
        log.info("角色权限分配完成: roleId={}", roleId);
    }
}

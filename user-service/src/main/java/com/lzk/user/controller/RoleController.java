package com.lzk.user.controller;

import com.lzk.common.result.Result;
import com.lzk.user.entity.Role;
import com.lzk.user.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 角色控制器
 * 处理角色管理相关请求
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 查询所有角色
     * @return 角色列表
     */
    @GetMapping("/list")
    public Result<List<Role>> listRoles() {
        log.info("查询所有角色");
        return Result.success(roleService.listRoles());
    }

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        log.info("查询角色: id={}", id);
        return Result.success(roleService.getRoleById(id));
    }

    /**
     * 添加角色
     * @param role 角色信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> addRole(@RequestBody Role role) {
        log.info("添加角色: roleName={}", role.getRoleName());
        roleService.addRole(role);
        return Result.success("角色添加成功");
    }

    /**
     * 更新角色
     * @param role 角色信息
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<String> updateRole(@RequestBody Role role) {
        log.info("更新角色: id={}, roleName={}", role.getId(), role.getRoleName());
        roleService.updateRole(role);
        return Result.success("角色更新成功");
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deleteRole(@PathVariable Long id) {
        log.info("删除角色: id={}", id);
        roleService.deleteRole(id);
        return Result.success("角色删除成功");
    }

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    @PostMapping("/assign/{userId}")
    public Result<String> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        log.info("为用户分配角色: userId={}, roleIds={}", userId, roleIds);
        roleService.assignRolesToUser(userId, roleIds);
        return Result.success("角色分配成功");
    }

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    @PostMapping("/permission/{roleId}")
    public Result<String> assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        log.info("为角色分配权限: roleId={}, permissionIds={}", roleId, permissionIds);
        roleService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success("权限分配成功");
    }

    /**
     * 查询用户的所有角色
     * @param userId 用户ID
     * @return 角色列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<Role>> getRolesByUserId(@PathVariable Long userId) {
        log.info("查询用户角色: userId={}", userId);
        List<Role> roles = roleService.getRolesByUserId(userId);
        log.info("用户角色查询结果: userId={}, roleCount={}", userId, roles.size());
        return Result.success(roles);
    }
}

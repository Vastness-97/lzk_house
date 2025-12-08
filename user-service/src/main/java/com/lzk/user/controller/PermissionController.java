package com.lzk.user.controller;

import com.lzk.common.result.Result;
import com.lzk.user.entity.Permission;
import com.lzk.user.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 权限控制器
 * 处理权限管理相关请求
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);
    
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 查询所有权限
     */
    @GetMapping("/list")
    public Result<List<Permission>> listPermissions() {
        log.info("查询所有权限");
        return Result.success(permissionService.listPermissions());
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    public Result<Permission> getPermissionById(@PathVariable Long id) {
        log.info("查询权限: id={}", id);
        return Result.success(permissionService.getPermissionById(id));
    }

    /**
     * 添加权限
     */
    @PostMapping("/add")
    public Result<String> addPermission(@RequestBody Permission permission) {
        log.info("添加权限: permissionName={}", permission.getPermissionName());
        permissionService.addPermission(permission);
        return Result.success("权限添加成功");
    }

    /**
     * 更新权限
     */
    @PutMapping("/update")
    public Result<String> updatePermission(@RequestBody Permission permission) {
        log.info("更新权限: id={}", permission.getId());
        permissionService.updatePermission(permission);
        return Result.success("权限更新成功");
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> deletePermission(@PathVariable Long id) {
        log.info("删除权限: id={}", id);
        permissionService.deletePermission(id);
        return Result.success("权限删除成功");
    }

    /**
     * 查询用户的所有权限
     */
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getPermissionsByUserId(@PathVariable Long userId) {
        log.info("查询用户权限: userId={}", userId);
        List<Permission> permissions = permissionService.getPermissionsByUserId(userId);
        log.info("用户权限查询结果: userId={}, permissionCount={}", userId, permissions.size());
        return Result.success(permissions);
    }
}

package com.lzk.user.controller.internal;

import com.lzk.user.entity.Permission;
import com.lzk.user.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission")
public class PermissionInternalController {

    private final PermissionService permissionService;

    public PermissionInternalController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/user/{userId}")
    public List<String> getPermissionCodesByUserId(@PathVariable Long userId) {
        return permissionService.getPermissionsByUserId(userId)
                .stream()
                .map(Permission::getPermissionCode)
                .collect(Collectors.toList());
    }
}

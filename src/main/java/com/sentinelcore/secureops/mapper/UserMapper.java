package com.sentinelcore.secureops.mapper;

import com.sentinelcore.secureops.dto.UserResponse;
import com.sentinelcore.secureops.model.Permission;
import com.sentinelcore.secureops.model.Role;
import com.sentinelcore.secureops.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        List<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        List<String> permissionNames = user.getRoles() != null
                ? user.getRoles().stream()
                        .filter(role -> role.getPermissions() != null)
                        .flatMap(role -> role.getPermissions().stream())
                        .map(Permission::getName)
                        .distinct()
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getOrganization(),
                user.isEnabled(),
                user.isLocked(),
                user.getLastLogin(),
                user.getPrimaryRoleName(),
                roleNames,
                permissionNames,
                user.getDesignation(),
                user.getDepartment(),
                user.getEmployeeId(),
                user.getTheme(),
                user.getNotifications(),
                user.getLanguage(),
                user.getTimezone(),
                user.getAvatar()
        );
    }
}

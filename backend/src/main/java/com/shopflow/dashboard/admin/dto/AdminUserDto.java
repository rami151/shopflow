package com.shopflow.dashboard.admin.dto;

import com.shopflow.shared.enums.Role;

import java.time.LocalDateTime;

public record AdminUserDto(
        Long id,
        String nom,
        String prenom,
        String email,
        Role role,
        boolean actif,
        LocalDateTime dateCreation
) {}


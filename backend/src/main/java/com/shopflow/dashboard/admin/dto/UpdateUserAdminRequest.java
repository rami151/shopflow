package com.shopflow.dashboard.admin.dto;

import com.shopflow.shared.enums.Role;
import jakarta.validation.constraints.Size;

public record UpdateUserAdminRequest(
        @Size(max = 100) String nom,
        @Size(max = 100) String prenom,
        Role role,
        Boolean actif
) {}


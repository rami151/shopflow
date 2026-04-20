package com.shopflow.dashboard.admin;

import com.shopflow.dashboard.admin.dto.AdminUserDto;
import com.shopflow.dashboard.admin.dto.UpdateUserAdminRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard/admin/users")
@RequiredArgsConstructor
@Tag(name = "Admin - Users", description = "Admin user management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @Operation(summary = "List users (ADMIN)")
    public ResponseEntity<List<AdminUserDto>> listUsers() {
        return ResponseEntity.ok(adminUserService.listUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by id (ADMIN)")
    public ResponseEntity<AdminUserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.getUser(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user fields (ADMIN)")
    public ResponseEntity<AdminUserDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserAdminRequest request
    ) {
        return ResponseEntity.ok(adminUserService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate user (ADMIN)")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        adminUserService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}


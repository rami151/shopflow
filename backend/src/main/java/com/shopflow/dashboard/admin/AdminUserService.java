package com.shopflow.dashboard.admin;

import com.shopflow.auth.UserRepository;
import com.shopflow.dashboard.admin.dto.AdminUserDto;
import com.shopflow.dashboard.admin.dto.UpdateUserAdminRequest;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AdminUserDto> listUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getDateCreation).reversed())
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminUserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return toDto(user);
    }

    @Transactional
    public AdminUserDto updateUser(Long id, UpdateUserAdminRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (request.nom() != null) user.setNom(request.nom());
        if (request.prenom() != null) user.setPrenom(request.prenom());
        if (request.role() != null) user.setRole(request.role());
        if (request.actif() != null) user.setActif(request.actif());

        User saved = userRepository.save(user);
        return toDto(saved);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setActif(false);
        userRepository.save(user);
    }

    private AdminUserDto toDto(User user) {
        return new AdminUserDto(
                user.getId(),
                user.getNom(),
                user.getPrenom(),
                user.getEmail(),
                user.getRole(),
                user.isActif(),
                user.getDateCreation()
        );
    }
}


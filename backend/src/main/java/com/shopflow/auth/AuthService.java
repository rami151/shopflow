package com.shopflow.auth;

import com.shopflow.auth.dto.AuthResponse;
import com.shopflow.auth.dto.LoginRequest;
import com.shopflow.auth.dto.RefreshRequest;
import com.shopflow.auth.dto.RegisterRequest;
import com.shopflow.product.SellerProfileRepository;
import com.shopflow.shared.entity.SellerProfile;
import com.shopflow.shared.entity.User;
import com.shopflow.shared.enums.Role;
import com.shopflow.shared.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }
        User user = User.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .role(request.role())
                .actif(true)
                .build();
        userRepository.save(user);

        if (request.role() == Role.SELLER) {
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .nomBoutique("Boutique de " + request.prenom() + " " + request.nom())
                    .description("Boutique du vendeur")
                    .actif(true)
                    .build();
            sellerProfileRepository.save(profile);
            user.setSellerProfile(profile);
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getRole().name());
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.motDePasse()));
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getRole().name());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String username = jwtUtil.extractUsername(request.refreshToken());
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!jwtUtil.isTokenValid(request.refreshToken(), user)) {
            throw new RuntimeException("Invalid refresh token");
        }
        String accessToken = jwtUtil.generateAccessToken(user);
        return new AuthResponse(accessToken, request.refreshToken(), user.getEmail(), user.getRole().name());
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}

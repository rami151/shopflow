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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SellerProfileRepository sellerProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest customerRegisterRequest;
    private RegisterRequest sellerRegisterRequest;
    private LoginRequest loginRequest;
    private RefreshRequest refreshRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        customerRegisterRequest = new RegisterRequest(
                "Doe",
                "John",
                "john.doe@example.com",
                "password123",
                Role.CUSTOMER
        );

        sellerRegisterRequest = new RegisterRequest(
                "Smith",
                "Jane",
                "jane.smith@example.com",
                "password123",
                Role.SELLER
        );

        loginRequest = new LoginRequest("john.doe@example.com", "password123");

        refreshRequest = new RefreshRequest("refreshToken");

        testUser = User.builder()
                .id(1L)
                .nom("Doe")
                .prenom("John")
                .email("john.doe@example.com")
                .motDePasse("encodedPassword")
                .role(Role.CUSTOMER)
                .actif(true)
                .build();
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register customer successfully")
        void shouldRegisterCustomerSuccessfully() {
            when(userRepository.existsByEmail(customerRegisterRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

            AuthResponse response = authService.register(customerRegisterRequest);

            assertNotNull(response);
            assertEquals("accessToken", response.accessToken());
            assertEquals("refreshToken", response.refreshToken());
            assertEquals("john.doe@example.com", response.email());
            assertEquals("CUSTOMER", response.role());
            verify(userRepository).save(any(User.class));
            verify(sellerProfileRepository, never()).save(any(SellerProfile.class));
        }

        @Test
        @DisplayName("Should register seller and create seller profile")
        void shouldRegisterSellerAndCreateSellerProfile() {
            when(userRepository.existsByEmail(sellerRegisterRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(sellerProfileRepository.save(any(SellerProfile.class))).thenAnswer(invocation -> {
                SellerProfile profile = invocation.getArgument(0);
                profile.setId(1L);
                return profile;
            });
            when(jwtUtil.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(jwtUtil.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

            AuthResponse response = authService.register(sellerRegisterRequest);

            assertNotNull(response);
            assertEquals("SELLER", response.role());
            verify(sellerProfileRepository).save(any(SellerProfile.class));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            when(userRepository.existsByEmail(customerRegisterRequest.email())).thenReturn(true);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> authService.register(customerRegisterRequest)
            );

            assertEquals("Email already registered", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully and return tokens")
        void shouldLoginSuccessfullyAndReturnTokens() {
            doAnswer(invocation -> null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(testUser));
            when(jwtUtil.generateAccessToken(testUser)).thenReturn("accessToken");
            when(jwtUtil.generateRefreshToken(testUser)).thenReturn("refreshToken");

            AuthResponse response = authService.login(loginRequest);

            assertNotNull(response);
            assertEquals("accessToken", response.accessToken());
            assertEquals("refreshToken", response.refreshToken());
            assertEquals("john.doe@example.com", response.email());
            assertEquals("CUSTOMER", response.role());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            doAnswer(invocation -> null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> authService.login(loginRequest)
            );

            assertEquals("User not found", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Refresh Tests")
    class RefreshTests {

        @Test
        @DisplayName("Should refresh token successfully")
        void shouldRefreshTokenSuccessfully() {
            when(jwtUtil.extractUsername(refreshRequest.refreshToken())).thenReturn("john.doe@example.com");
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(jwtUtil.isTokenValid(refreshRequest.refreshToken(), testUser)).thenReturn(true);
            when(jwtUtil.generateAccessToken(testUser)).thenReturn("newAccessToken");

            AuthResponse response = authService.refresh(refreshRequest);

            assertNotNull(response);
            assertEquals("newAccessToken", response.accessToken());
            assertEquals(refreshRequest.refreshToken(), response.refreshToken());
        }

        @Test
        @DisplayName("Should throw exception when user not found on refresh")
        void shouldThrowExceptionWhenUserNotFoundOnRefresh() {
            when(jwtUtil.extractUsername(refreshRequest.refreshToken())).thenReturn("john.doe@example.com");
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> authService.refresh(refreshRequest)
            );

            assertEquals("User not found", exception.getMessage());
        }

        @Test
        @DisplayName("Should throw exception when refresh token is invalid")
        void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
            when(jwtUtil.extractUsername(refreshRequest.refreshToken())).thenReturn("john.doe@example.com");
            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));
            when(jwtUtil.isTokenValid(refreshRequest.refreshToken(), testUser)).thenReturn(false);

            RuntimeException exception = assertThrows(
                    RuntimeException.class,
                    () -> authService.refresh(refreshRequest)
            );

            assertEquals("Invalid refresh token", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should clear security context on logout")
        void shouldClearSecurityContextOnLogout() {
            assertDoesNotThrow(() -> authService.logout());
        }
    }
}

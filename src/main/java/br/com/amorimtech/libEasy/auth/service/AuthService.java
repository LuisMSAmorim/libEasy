package br.com.amorimtech.libEasy.auth.service;


import br.com.amorimtech.libEasy.auth.dto.AuthRequest;
import br.com.amorimtech.libEasy.auth.dto.AuthResponse;
import br.com.amorimtech.libEasy.auth.dto.RegisterRequest;
import br.com.amorimtech.libEasy.auth.model.Role;
import br.com.amorimtech.libEasy.auth.model.User;
import br.com.amorimtech.libEasy.auth.repository.UserRepository;
import br.com.amorimtech.libEasy.auth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req, Role role) {
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use");
        }
        var user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .role(role)
                .build();
        userRepository.save(user);
        return AuthResponse.of(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponse login(AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var user = userRepository.findByEmail(req.email()).orElseThrow();
        return AuthResponse.of(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user));
    }

    public AuthResponse refresh(String refreshToken) {
        var email = jwtService.extractUsername(refreshToken);
        var user = userRepository.findByEmail(email).orElseThrow();
        // valida se é refresh token (checar claim opcionalmente)
        return AuthResponse.of(jwtService.generateAccessToken(user), jwtService.generateRefreshToken(user));
    }
}

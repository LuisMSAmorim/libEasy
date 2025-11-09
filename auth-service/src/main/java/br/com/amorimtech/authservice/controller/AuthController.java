package br.com.amorimtech.authservice.controller;

import br.com.amorimtech.authservice.dto.AuthRequest;
import br.com.amorimtech.authservice.dto.AuthResponse;
import br.com.amorimtech.authservice.dto.RegisterRequest;
import br.com.amorimtech.authservice.model.Role;
import br.com.amorimtech.authservice.security.JwtService;
import br.com.amorimtech.authservice.service.AuthService;
import br.com.amorimtech.authservice.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        var resp = authService.register(req, Role.USER);
        return ApiResponse.success(resp, HttpStatus.CREATED).createResponseEntity();
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest req) {
        var resp = authService.login(req);
        return ApiResponse.success(resp, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestHeader("Authorization") String bearer) {
        var token = bearer.replace("Bearer ", "");
        var resp = authService.refresh(token);
        return ApiResponse.success(resp, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping(value = "/public-key", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getPublicKey() throws Exception {
        return ResponseEntity.ok(jwtService.getPublicKeyPEM());
    }
}

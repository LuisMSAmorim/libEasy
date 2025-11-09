package br.com.amorimtech.libEasy.auth.controller;


import br.com.amorimtech.libEasy.auth.dto.AuthRequest;
import br.com.amorimtech.libEasy.auth.dto.AuthResponse;
import br.com.amorimtech.libEasy.auth.dto.RegisterRequest;
import br.com.amorimtech.libEasy.auth.model.Role;
import br.com.amorimtech.libEasy.auth.service.AuthService;
import br.com.amorimtech.libEasy.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

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
}

package br.com.amorimtech.libEasy.auth.dto;


public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {
    public static AuthResponse of(String access, String refresh) {
        return new AuthResponse(access, refresh, "Bearer");
    }
}

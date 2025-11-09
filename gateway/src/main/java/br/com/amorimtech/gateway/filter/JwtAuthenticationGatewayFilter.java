package br.com.amorimtech.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationGatewayFilter extends AbstractGatewayFilterFactory<JwtAuthenticationGatewayFilter.Config> {

    private PublicKey publicKey;
    private final String issuer;
    private final String audience;

    public JwtAuthenticationGatewayFilter(
            @Value("${jwt.public-key-path:/keys/public_key.pem}") String publicKeyPath,
            @Value("${jwt.issuer:libEasy}") String issuer,
            @Value("${jwt.audience:libEasy-api}") String audience
    ) {
        super(Config.class);
        this.issuer = issuer;
        this.audience = audience;
        try {
            this.publicKey = loadPublicKey(publicKeyPath);
            log.info("JWT Public Key loaded successfully from: {}", publicKeyPath);
        } catch (Exception e) {
            log.error("Failed to load JWT public key from: {}", publicKeyPath, e);
            throw new RuntimeException("Failed to initialize JWT authentication filter", e);
        }
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(path)))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Extract Authorization header
            List<String> authHeaders = request.getHeaders().get("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                log.warn("Missing Authorization header for path: {}", request.getPath());
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = authHeaders.get(0);
            if (!authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format for path: {}", request.getPath());
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Validate and parse JWT
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(publicKey)
                        .requireIssuer(issuer)
                        .requireAudience(audience)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                // Extract user info from claims
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                Object userIdObj = claims.get("userId");
                String name = claims.get("name", String.class);

                // Convert userId to String (it might be Integer or Long)
                String userId = userIdObj != null ? String.valueOf(userIdObj) : null;

                if (email == null || role == null || userId == null) {
                    log.warn("Missing required claims in JWT token");
                    return onError(exchange, "Invalid token claims", HttpStatus.UNAUTHORIZED);
                }

                log.debug("JWT validated successfully for user: {} (role: {})", email, role);

                // Add custom headers with user info
                ServerHttpRequest mutatedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-User-Email", email)
                        .header("X-User-Role", role)
                        .header("X-User-Name", name != null ? name : "")
                        .build();

                ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(mutatedRequest)
                        .build();

                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage());
                return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        String errorBody = String.format(
            "{\"success\":false,\"errorMessage\":\"%s\",\"data\":null}",
            message
        );

        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(errorBody.getBytes()))
        );
    }

    public static class Config {
        // Configuration properties (if needed in the future)
    }
}

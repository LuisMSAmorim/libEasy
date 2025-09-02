package br.com.amorimtech.libEasy.auth.security;


import br.com.amorimtech.libEasy.auth.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long accessTtlMs;
    private final long refreshTtlMs;
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${app.jwt.secret}") String secretBase64,
            @Value("${app.jwt.access-ttl-ms:900000}") long accessTtlMs,      // 15 min
            @Value("${app.jwt.refresh-ttl-ms:1209600000}") long refreshTtlMs, // 14 dias
            @Value("${app.jwt.issuer:libEasy}") String issuer,
            @Value("${app.jwt.audience:libEasy-api}") String audience
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64));
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
        this.issuer = issuer;
        this.audience = audience;
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessTtlMs, Map.of("role", user.getRole().name()));
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshTtlMs, Map.of("type", "refresh"));
    }

    private String buildToken(User user, long ttl, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(ttl)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parseAllClaims(token).getSubject();
    }

    public boolean isValid(String token, User user) {
        Claims c = parseAllClaims(token);
        return user.getEmail().equals(c.getSubject()) && c.getExpiration().after(new Date());
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireAudience(audience)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

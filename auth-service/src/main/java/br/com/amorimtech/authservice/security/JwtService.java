package br.com.amorimtech.authservice.security;

import br.com.amorimtech.authservice.model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private final long accessTtlMs;
    private final long refreshTtlMs;
    private final String issuer;
    private final String audience;

    public JwtService(
            @Value("${jwt.private-key-path}") String privateKeyPath,
            @Value("${jwt.public-key-path}") String publicKeyPath,
            @Value("${jwt.access-token-ttl:900000}") long accessTtlMs,
            @Value("${jwt.refresh-token-ttl:1209600000}") long refreshTtlMs,
            @Value("${jwt.issuer:libEasy}") String issuer,
            @Value("${jwt.audience:libEasy-api}") String audience
    ) throws Exception {
        this.privateKey = loadPrivateKey(privateKeyPath);
        this.publicKey = loadPublicKey(publicKeyPath);
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
        this.issuer = issuer;
        this.audience = audience;
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(java.nio.file.Paths.get(path)))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(java.nio.file.Paths.get(path)))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public String generateAccessToken(User user) {
        return buildToken(user, accessTtlMs, Map.of(
                "role", user.getRole().name(),
                "userId", user.getId(),
                "name", user.getName()
        ));
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
                .signWith(privateKey, SignatureAlgorithm.RS256)
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
                .setSigningKey(publicKey)
                .requireAudience(audience)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getPublicKeyPEM() throws Exception {
        byte[] encoded = publicKey.getEncoded();
        String base64 = Base64.getEncoder().encodeToString(encoded);
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----";
    }
}

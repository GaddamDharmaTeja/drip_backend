package com.dripgrow.api.security;

import com.dripgrow.api.model.AppSettings;
import com.dripgrow.api.repository.AppSettingsRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final AppSettingsRepository appSettingsRepository;
    private final String fallbackSecret;
    private final long expirationMs;
    private volatile SecretKey cachedKey;

    public JwtService(
            AppSettingsRepository appSettingsRepository,
            @Value("${dripgrow.jwt.secret}") String fallbackSecret,
            @Value("${dripgrow.jwt.expiration-ms}") long expirationMs
    ) {
        this.appSettingsRepository = appSettingsRepository;
        this.fallbackSecret = fallbackSecret;
        this.expirationMs = expirationMs;
    }

    /** Call after seeding/updating JWT secret in MongoDB. */
    public void reloadSecret() {
        cachedKey = null;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(exp)
                .signWith(resolveKey())
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isValid(String token, String username) {
        Claims claims = parseClaims(token);
        return claims.getSubject().equals(username) && claims.getExpiration().after(new Date());
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(resolveKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey resolveKey() {
        SecretKey local = cachedKey;
        if (local != null) {
            return local;
        }
        synchronized (this) {
            if (cachedKey == null) {
                String secret = appSettingsRepository.findById(AppSettings.DEFAULT_ID)
                        .map(AppSettings::getJwtSecret)
                        .filter(s -> s != null && !s.isBlank())
                        .orElse(fallbackSecret);
                byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
                if (bytes.length < 32) {
                    // HMAC-SHA keys must be >= 256 bits
                    bytes = (secret + "0123456789abcdef0123456789abcdef").getBytes(StandardCharsets.UTF_8);
                }
                cachedKey = Keys.hmacShaKeyFor(bytes);
            }
            return cachedKey;
        }
    }
}

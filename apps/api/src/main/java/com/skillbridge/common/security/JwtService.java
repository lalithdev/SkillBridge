package com.skillbridge.common.security;

import com.skillbridge.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long jwtExpiryMs;

    public JwtService(
            @Value("${app.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secret,
            @Value("${app.jwt.expiry-ms:86400000}") long jwtExpiryMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpiryMs = jwtExpiryMs;
    }

    public String generateToken(Long userId, String email, Role role, Long collegeId, Long companyProfileId, Long studentProfileId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role.name());
        if (collegeId != null) {
            claims.put("collegeId", collegeId);
        }
        if (companyProfileId != null) {
            claims.put("companyProfileId", companyProfileId);
        }
        if (studentProfileId != null) {
            claims.put("studentProfileId", studentProfileId);
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiryMs);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        String subject = extractClaim(token, Claims::getSubject);
        return subject != null ? Long.parseLong(subject) : null;
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    public Role extractRole(String token) {
        Claims claims = extractAllClaims(token);
        String roleStr = claims.get("role", String.class);
        return roleStr != null ? Role.valueOf(roleStr) : null;
    }

    public Long extractCollegeId(String token) {
        Claims claims = extractAllClaims(token);
        Object idObj = claims.get("collegeId");
        return idObj != null ? ((Number) idObj).longValue() : null;
    }

    public Long extractCompanyProfileId(String token) {
        Claims claims = extractAllClaims(token);
        Object idObj = claims.get("companyProfileId");
        return idObj != null ? ((Number) idObj).longValue() : null;
    }

    public Long extractStudentProfileId(String token) {
        Claims claims = extractAllClaims(token);
        Object idObj = claims.get("studentProfileId");
        return idObj != null ? ((Number) idObj).longValue() : null;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

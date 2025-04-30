package tw.eeits.unhappy.ll.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, String role, Integer userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + 86400000); // 1 天

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claim("role", role)
                .claim("uid", userId)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> validateAndParseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public String extractUsername(String token) {
        return validateAndParseToken(token).getPayload().getSubject();
    }

    public String extractRole(String token) {
        return (String) validateAndParseToken(token).getPayload().get("role");
    }

    public Integer extractUserId(String token) {
        return (Integer) validateAndParseToken(token).getPayload().get("uid");
    }
}


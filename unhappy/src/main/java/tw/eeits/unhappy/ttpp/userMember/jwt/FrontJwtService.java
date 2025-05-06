package tw.eeits.unhappy.ttpp.userMember.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class FrontJwtService {

    @Value("${jwt.front.secret}") // 和後台不同的 secret key
    private String jwtFrontSecret;

    @Value("${jwt.front.expiration}") // 和後台不同的過期時間
    private long jwtFrontExpiration;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(jwtFrontSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Integer memberId) { // 只需要 username 和 memberId
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtFrontExpiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .claim("memberId", memberId) // 使用不同的 claim
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

    public Integer extractMemberId(String token) {
        return (Integer) validateAndParseToken(token).getPayload().get("memberId");
    }
}



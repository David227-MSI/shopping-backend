package tw.eeits.unhappy.ttpp.userMember.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.security.JwtService;

@Component
@RequiredArgsConstructor
public class UserJwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // 註冊不需要被JWT驗證攔截的白名單
    private static final List<String> USER_WHITELIST = List.of(
        "/api/user/secure/login",
        "/api/user/register"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 檢查白名單
        if (USER_WHITELIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 檢查帶Authorization的header
        // 如果不是"Bearer "前綴, 回傳401和錯誤訊息
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未提供Token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Jws<Claims> claimsJws = jwtService.validateAndParseToken(token);
            Claims claims = claimsJws.getBody();
            // 檢查role是不是USER
            if (!"USER".equals(claims.get("role", String.class))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "權限不足");
                return;
            }

            request.setAttribute("userId", claims.get("uid", Integer.class));
            request.setAttribute("username", claims.getSubject());

            // 如果檢查無問題, 放行請求和回應
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "無效Token");
        }
    }

    // 決定哪些API可以繞過攔截器
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // 只攔截 /api/user/* 開頭的 API
        return !request.getRequestURI().startsWith("/api/user/*");
    }
}


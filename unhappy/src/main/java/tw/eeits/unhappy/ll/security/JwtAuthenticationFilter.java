package tw.eeits.unhappy.ll.security;

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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    // 不需要登入的路徑 (白名單)
    private static final List<String> VISITOR_WHITELIST = List.of(
            
            "/api/contact/public",
            "/api/admin/login",
            // "/api/sales-report"
            "/api/admin/coupons/getValidCoupon"
            );

    // 只需要有Token（不限角色）即可使用的API (例如: 改自己密碼)
    private static final List<String> AUTHENTICATED_API = List.of(
            "/api/admin/change-password");

    // 只允許 MANAGER 存取的API (例如: 員工管理)
    private static final List<String> MANAGER_ONLY_API = List.of(
            "/api/admin/users");




    // 允許 MANAGER 和 STAFF 都能操作的API (例如: 品牌管理、客服處理)
    private static final List<String> MANAGER_AND_STAFF_API = List.of(
            "/api/admin/brands",
            "/api/contact/messages",
            "/api/sales-report"
            );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 白名單: 不需要帶Token
        if (isVisitorPath(path, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 其他API都需要Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未提供Token");
            return;
        }

        String token = authHeader.substring(7);

        try {
            Jws<Claims> claimsJws = jwtService.validateAndParseToken(token);
            Claims claims = claimsJws.getPayload();

            // 把解析後的資訊放進 request attribute
            request.setAttribute("userId", claims.get("uid", Integer.class));
            request.setAttribute("username", claims.getSubject());
            request.setAttribute("role", claims.get("role", String.class));

            String role = claims.get("role", String.class);

            // 根據不同API分類控制權限
            if (isManagerOnlyPath(path)) {
                if (!"MANAGER".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "需要MANAGER權限");
                    return;
                }
            } else if (isManagerAndStaffPath(path)) {
                if (!"MANAGER".equals(role) && !"STAFF".equals(role)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "需要MANAGER或STAFF權限");
                    return;
                }
            } else if (isAuthenticatedPath(path)) {
                // 只要有登入就可以，不驗證角色
            } else {
                // 其他未定義的API，預設禁止（可以依專案需求調整）
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "禁止存取");
                return;
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "無效Token");
        }
    }

    private boolean isVisitorPath(String path, String method) {
        // return "POST".equals(method) && VISITOR_WHITELIST.stream().anyMatch(path::startsWith);
        return VISITOR_WHITELIST.stream().anyMatch(path::startsWith);   
    }

    private boolean isAuthenticatedPath(String path) {
        return AUTHENTICATED_API.stream().anyMatch(path::startsWith);
    }

    private boolean isManagerOnlyPath(String path) {
        return MANAGER_ONLY_API.stream().anyMatch(path::startsWith);
    }

    private boolean isManagerAndStaffPath(String path) {
        return MANAGER_AND_STAFF_API.stream().anyMatch(path::startsWith);
    }
}



// package tw.eeits.unhappy.ll.security;

// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerInterceptor;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jws;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationFilter implements HandlerInterceptor {

// private final JwtService jwtService;

// @Override
// public boolean preHandle(HttpServletRequest request, HttpServletResponse
// response, Object handler) throws Exception {
// String authHeader = request.getHeader("Authorization");

// if (authHeader == null || !authHeader.startsWith("Bearer ")) {
// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未提供 Token");
// return false;
// }

// String token = authHeader.substring(7); // 去掉 "Bearer "

// try {
// Jws<Claims> claimsJws = jwtService.validateAndParseToken(token);
// Claims claims = claimsJws.getPayload();

// // 儲存資訊到 request 層，供後續使用
// request.setAttribute("userId", claims.get("uid", Integer.class));
// request.setAttribute("username", claims.getSubject());
// request.setAttribute("role", claims.get("role", String.class));

// return true;
// } catch (Exception e) {
// response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "無效 Token");
// return false;
// }
// }
// }

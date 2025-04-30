package tw.eeits.unhappy.ll.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.dto.LoginRequest;
import tw.eeits.unhappy.ll.dto.LoginResponse;
import tw.eeits.unhappy.ll.dto.LoginResult;
import tw.eeits.unhappy.ll.model.AdminUser;
import tw.eeits.unhappy.ll.security.JwtService;
import tw.eeits.unhappy.ll.service.AdminUserService;
import tw.eeits.unhappy.ll.util.IpUtils;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminLoginController {

    private final AdminUserService adminUserService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        // 抓 IP
        String ip = IpUtils.getClientIp(httpRequest);

        LoginResult result = adminUserService.login(request.getUsername(), request.getPassword(),ip);
        AdminUser user = result.getUser();


        System.out.println(user);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole(), user.getId());

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole())
                .firstLoginAt(result.isFirstLogin() ? null : user.getFirstLoginAt())
                .build();

        return ResponseEntity.ok(response);
    }
}

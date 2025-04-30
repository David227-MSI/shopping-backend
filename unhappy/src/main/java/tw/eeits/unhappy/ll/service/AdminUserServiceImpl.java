package tw.eeits.unhappy.ll.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.dto.AdminUserResponse;
import tw.eeits.unhappy.ll.dto.ChangePasswordRequest;
import tw.eeits.unhappy.ll.dto.CreateAdminUserRequest;
import tw.eeits.unhappy.ll.dto.LoginResult;
import tw.eeits.unhappy.ll.dto.UpdateAdminUserRequest;
import tw.eeits.unhappy.ll.model.AdminLoginLog;
import tw.eeits.unhappy.ll.model.AdminUser;
import tw.eeits.unhappy.ll.model.AdminUserStatus;
import tw.eeits.unhappy.ll.repository.AdminLoginLogRepository;
import tw.eeits.unhappy.ll.repository.AdminUserRepository;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    
    private final AdminUserRepository adminUserRepository;
    private final AdminLoginLogRepository adminLoginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "PENDING", "SUSPENDED", "INACTIVE");
    private static final Set<String> VALID_ROLES = Set.of("MANAGER", "STAFF");

    @Override
    public List<AdminUserResponse> findAll(String status, String role) {
        List<AdminUser> users = adminUserRepository.findAll();

        // 篩選 status
        if (status != null && !status.isBlank()) {
            String normalizedStatus = status.toUpperCase();

            // ✅ 加入驗證
            if (!VALID_STATUSES.contains(normalizedStatus)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無效的帳號狀態參數");
            }

            users = users.stream()
                    .filter(u -> u.getStatus().name().equals(normalizedStatus))
                    .toList();
        }

        // 篩選 role
        if (role != null && !role.isBlank()) {
            String normalizedRole = role.toUpperCase();

            // ✅ 加入驗證
            if (!VALID_ROLES.contains(normalizedRole)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無效的角色參數");
            }

            users = users.stream()
                    .filter(u -> u.getRole().equals(normalizedRole))
                    .toList();
        }

        return users.stream()
                .map(user -> AdminUserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .status(user.getStatus().name())
                        .createdAt(user.getCreatedAt())
                        .updatedAt(user.getUpdatedAt())
                        .build())
                .toList();
    }

    @Override
    public AdminUserResponse findAdminUserById(Integer id) {
        AdminUser user = adminUserRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定帳號"));

        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus().name()) // enum 轉 string
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public LoginResult login(String username, String password, String ipAddress) {

        AdminUser user = adminUserRepository.findByUsername(username).orElse(null);
        boolean success = false;
        String message;
        boolean isFirstLogin = (user != null && user.getFirstLoginAt() == null);

        if (user == null) {
            message = "帳號不存在";
        } else if (!passwordEncoder.matches(password, user.getPassword())) {
            message = "密碼錯誤";
        } else if (user.getStatus() != AdminUserStatus.ACTIVE && user.getStatus() != AdminUserStatus.PENDING) {
            message = "帳號已停用";
        } else {
            success = true;

            // 若帳號為 PENDING，轉為 ACTIVE 並寫入首次登入時間
            if (user.getStatus() == AdminUserStatus.PENDING) {
                user.setStatus(AdminUserStatus.ACTIVE);
                user.setFirstLoginAt(LocalDateTime.now());
                user.setUpdatedAt(LocalDateTime.now());
                adminUserRepository.save(user);
            }

            message = "登入成功";
        }
        // System.out.println("error: "+message);
        AdminLoginLog log = AdminLoginLog.builder()
                .username(username)
                .ipAddress(ipAddress) // TODO: 從 request 或攔截器中取得真實 IP
                .loginTime(LocalDateTime.now())
                .success(success)
                .message(message)
                .build();
        adminLoginLogRepository.save(log);

        if (!success) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤");
        }

        return new LoginResult(user, isFirstLogin);
    }

    @Override
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        AdminUser user = adminUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "使用者不存在"));

        // 若已登入過，驗證原密碼（第一次登入不檢查）
        if (user.getFirstLoginAt() != null && request.getOldPassword() != null) {
            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "原密碼錯誤");
            }
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        adminUserRepository.save(user);
    }

    @Override
    public void updateAdminUser(Integer targetUserId, UpdateAdminUserRequest request, Integer actingUserId) {
        AdminUser user = adminUserRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "帳號不存在"));

        // 禁止 manager 修改自己的 role
        if (targetUserId.equals(actingUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "禁止修改自己的角色或狀態");
        }

        // 驗證 role 合法
        String normalizedRole = request.getRole().toUpperCase();
        if (!VALID_ROLES.contains(normalizedRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "角色必須是 MANAGER 或 STAFF");
        }

        // 驗證 status 合法
        String normalizedStatus = request.getStatus().toUpperCase();
        if (!VALID_STATUSES.contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "帳號狀態不合法");
        }

        user.setRole(request.getRole().toUpperCase());
        user.setStatus(AdminUserStatus.valueOf(normalizedStatus));
        user.setUpdatedAt(LocalDateTime.now());

        adminUserRepository.save(user);
    }

    @Override
    public void createAdminUser(CreateAdminUserRequest request) {
        // 檢查 username 是否已存在
        if (adminUserRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在，請使用其他帳號");
        }

        // 驗證角色
        String normalizedRole = request.getRole().toUpperCase();
        if (!"MANAGER".equals(normalizedRole) && !"STAFF".equals(normalizedRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "角色必須是 MANAGER 或 STAFF");
        }

        // 密碼加密
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 建立新 admin user
        AdminUser newUser = AdminUser.builder()
                .username(request.getUsername())
                .fullName(request.getFullName())
                .role(normalizedRole)
                .password(encodedPassword)
                .status(AdminUserStatus.PENDING) // 一律 PENDING
                .build();

        adminUserRepository.save(newUser);
    }

}

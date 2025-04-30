package tw.eeits.unhappy.ll.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ll.dto.AdminUserResponse;
import tw.eeits.unhappy.ll.dto.ChangePasswordRequest;
import tw.eeits.unhappy.ll.dto.CreateAdminUserRequest;
import tw.eeits.unhappy.ll.dto.UpdateAdminUserRequest;
import tw.eeits.unhappy.ll.model.AdminUser;
import tw.eeits.unhappy.ll.service.AdminUserService;
import tw.eeits.unhappy.ll.util.RoleUtils;

@RestController
@RequestMapping("/api/admin") // ← 建議對應資源分類
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping("/users")
    public List<AdminUserResponse> getUsers(@RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "role", required = false) String role, HttpServletRequest request) {
        RoleUtils.assertManager(request);

        return adminUserService.findAll(status, role);
    }

    @GetMapping("/users/{id}")
    public AdminUserResponse getAdminUserById(@PathVariable("id") Integer id, HttpServletRequest request) {
        RoleUtils.assertManager(request); // 只有 manager 可以查

        return adminUserService.findAdminUserById(id);
    }

    // @PutMapping("/change-password")
    // public void changePassword(@RequestBody @Valid ChangePasswordRequest request,
    // HttpServletRequest httpRequest) {
    // Integer userId = (Integer) httpRequest.getAttribute("userId");
    // adminUserService.changePassword(userId, request);
    // }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {

        Integer userId = (Integer) httpRequest.getAttribute("userId");
        adminUserService.changePassword(userId, request);

        return ResponseEntity.ok("密碼修改成功");
    }

    @PutMapping("/users/{id}")
    public void updateAdminUser(@PathVariable("id") Integer id,
            @RequestBody @Valid UpdateAdminUserRequest request,
            HttpServletRequest httpRequest) {

        RoleUtils.assertManager(httpRequest); // 確認呼叫者是 manager

        Integer actingUserId = (Integer) httpRequest.getAttribute("userId");

        adminUserService.updateAdminUser(id, request, actingUserId);
    }

    @PostMapping
    public void createAdminUser(@RequestBody @Valid CreateAdminUserRequest request,
            HttpServletRequest httpRequest) {

        RoleUtils.assertManager(httpRequest); // 確認呼叫者是 manager

        adminUserService.createAdminUser(request);
    }

}

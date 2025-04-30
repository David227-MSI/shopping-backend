package tw.eeits.unhappy.ll.service;

import java.util.List;

import tw.eeits.unhappy.ll.dto.AdminUserResponse;
import tw.eeits.unhappy.ll.dto.ChangePasswordRequest;
import tw.eeits.unhappy.ll.dto.CreateAdminUserRequest;
import tw.eeits.unhappy.ll.dto.LoginResult;
import tw.eeits.unhappy.ll.dto.UpdateAdminUserRequest;


public interface AdminUserService {
    LoginResult login(String username, String password, String ipAddress);
    List<AdminUserResponse> findAll(String status, String role);
    void changePassword(Integer userId, ChangePasswordRequest request);
    void updateAdminUser(Integer targetUserId, UpdateAdminUserRequest request, Integer actingUserId);
    void createAdminUser(CreateAdminUserRequest request);
    AdminUserResponse findAdminUserById(Integer id);



}
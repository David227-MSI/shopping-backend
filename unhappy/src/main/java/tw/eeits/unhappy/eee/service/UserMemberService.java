package tw.eeits.unhappy.eee.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.repository.UserMemberRepository;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.userMember.dto.UserModifyRequest;
import tw.eeits.unhappy.ttpp.userMember.dto.UserRegisterRequest;

@Service
@RequiredArgsConstructor
public class UserMemberService {
    private final UserMemberRepository userMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

    public UserMember findUserById(Integer id) {
        return userMemberRepository.findById(id).orElse(null);
    }

    // 驗證登入
    public ServiceResponse<UserMember> authenticate(String email, String rawPassword) {

        // PasswordEncoder encoder = new BCryptPasswordEncoder();
        // String encoded = encoder.encode("Test@1234");
        // System.out.println("加密後密碼: " + encoded);

        ErrorCollector ec = new ErrorCollector();

        UserMember foundUser = null;

        if(email == null) {
            ec.add("請輸入email");
        } else if(rawPassword == null) {
            ec.add("請輸入密碼");
        } else {
            // check userMember
            foundUser = userMemberRepository.findByEmail(email).orElse(null);
        }
        
        if (foundUser == null) {
            ec.add("信箱或密碼錯誤");
        } else {
            // check password
            if (!passwordEncoder.matches(rawPassword, foundUser.getPassword())) {
                ec.add("信箱或密碼錯誤");
            }
        }

        if(ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        return ServiceResponse.success(foundUser);
    }


    public ServiceResponse<UserMember> userModify(UserModifyRequest request) {
    
        if (request == null) {
            return ServiceResponse.fail("請求不能為空");
        }
    
        UserMember foundUser = userMemberRepository.findById(request.getId()).orElse(null);
        if (foundUser == null) {
            return ServiceResponse.fail("找不到目標用戶");
        }
    
        if (request.getEmail() != null) {
            foundUser.setEmail(request.getEmail());
        }
        if (request.getUsername() != null) {
            foundUser.setUsername(request.getUsername());
        }
        if (request.getPhone() != null) {
            foundUser.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            foundUser.setAddress(request.getAddress());
        }

        // check password
        if (request.getNewPassword() != null) {
            // verify oldPassword 
            if (request.getOldPassword() == null || !passwordEncoder.matches(request.getOldPassword(), foundUser.getPassword())) {
                return ServiceResponse.fail("輸入的舊密碼不正確");
            }

            System.out.println("old pwd: " + request.getOldPassword());
            System.out.println("new pwd: " + request.getNewPassword());
            foundUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }
    
        try {
            UserMember updatedUser = userMemberRepository.save(foundUser);
            return ServiceResponse.success(updatedUser);
        } catch (Exception e) {
            return ServiceResponse.fail("更新用戶資料失敗: " + e.getMessage());
        }
    }




    @Transactional
    public ServiceResponse<UserMember> register(
            UserRegisterRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        ec.validate(request, validator);

        // check password
        if (!request.getPassword().equals(request.getComfirmPassword())) {
            ec.add("確認密碼與密碼不一致");
        }

        // check email
        UserMember foundUser = userMemberRepository.findByEmail(request.getEmail()).orElse(null);
        if (foundUser != null) {
            ec.add("該電子郵件已被註冊");
        }

        if (ec.hasErrors()) {
            return ServiceResponse.fail(ec.getErrorMessage());
        }

        // service operation
        UserMember newEntry = new UserMember();
        newEntry.setEmail(request.getEmail());
        newEntry.setUsername(request.getUsername());
        newEntry.setBirth(request.getBirth());
        newEntry.setPhone(request.getPhone());
        newEntry.setAddress(request.getAddress());

        // encode
        String pwd = passwordEncoder.encode(request.getPassword());
        newEntry.setPassword(pwd);

        try {
            UserMember savedEntry = userMemberRepository.save(newEntry);
            return ServiceResponse.success(savedEntry);
        } catch (Exception e) {
            return ServiceResponse.fail("註冊帳號發生異常，請稍後再試");
        }

    }



}
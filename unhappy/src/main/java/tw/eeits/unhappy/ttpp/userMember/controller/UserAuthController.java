package tw.eeits.unhappy.ttpp.userMember.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.service.UserMemberService;
import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ErrorCollector;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;
import tw.eeits.unhappy.ttpp._response.ServiceResponse;
import tw.eeits.unhappy.ttpp.email.EmailService;
import tw.eeits.unhappy.ttpp.email.dto.VerifyEmailRequest;
import tw.eeits.unhappy.ttpp.userMember.dto.ResendVerifyEmailRequest;
import tw.eeits.unhappy.ttpp.userMember.dto.UserEmail;
import tw.eeits.unhappy.ttpp.userMember.dto.UserLoginRequest;
import tw.eeits.unhappy.ttpp.userMember.dto.UserRegisterRequest;
import tw.eeits.unhappy.ttpp.userMember.jwt.FrontJwtService;
import tw.eeits.unhappy.ttpp.userMember.model.EmailToken;
import tw.eeits.unhappy.ttpp.userMember.service.EmailTokenService;

@RestController
@RequestMapping("/api/user/secure")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserMemberService userMemberService; // 處理帳號密碼驗證
    private final FrontJwtService jwtService;
    private final Validator validator;
    private final EmailService emailService;
    private final EmailTokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiRes<Map<String, Object>>> login(
        @RequestBody UserLoginRequest request
    ) {

        ErrorCollector ec = new ErrorCollector();

        ec.validate(request, validator);

        // check request
        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<UserMember> res = userMemberService.authenticate(request.getEmail(), request.getPassword());

        if(!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        UserMember foundUser = res.getData();
        String token = jwtService.generateToken(foundUser.getUsername(), foundUser.getId());

        Map<String, Object> data = new HashMap();
        data.put("token", token);
        data.put("userId", foundUser.getId());
        data.put("username", foundUser.getUsername());
        data.put("email", foundUser.getEmail());
        data.put("phone", foundUser.getPhone());
        data.put("address", foundUser.getAddress());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }



    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Map<String, Object>>> logout() {
        // 在這裡可以執行後端登出相關的清理操作 (如果需要)

        Map<String, Object> data = new HashMap<>();
        data.put("message", "登出成功"); // 向前端發送登出成功的消息

        return ResponseEntity.ok(ResponseFactory.success(data));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiRes<Map<String, Object>>> register(
        @RequestBody UserRegisterRequest request
    ) {
        ErrorCollector ec = new ErrorCollector();

        if (request == null) {
            ec.add("註冊發生異常, 請稍後再嘗試");
        } else {
            ec.validate(request, validator);
        }

        UserMember existingUser = userMemberService.findUserByEmail(request.getEmail());
        if(existingUser != null) {
            ec.add("此信箱已經註冊過");
        }

        if (ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseFactory.fail(ec.getErrorMessage()));
        }


        // call service
        String email = request.getEmail();
        String verificationCode = request.getVerificationCode();

        try {
            boolean isVerified = emailService.verifyToken(email, verificationCode);
            if (isVerified) {
            // 驗證成功，執行使用者註冊
            ServiceResponse<UserMember> registerRes = userMemberService.register(request);

            if (!registerRes.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.fail(registerRes.getMessage()));
            }

            UserMember registeredUser = registerRes.getData();

            // 自動登入並生成 JWT Token
            ServiceResponse<UserMember> authRes = userMemberService.authenticate(request.getEmail(), request.getPassword());

            if (!authRes.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.fail(authRes.getMessage()));
            }

            UserMember foundUser = authRes.getData();
            String token = jwtService.generateToken(foundUser.getUsername(), foundUser.getId());

            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", foundUser.getId());
            data.put("username", foundUser.getUsername());
            data.put("email", foundUser.getEmail());
            data.put("phone", foundUser.getPhone());
            data.put("address", foundUser.getAddress());

            return ResponseEntity.ok(ResponseFactory.success(data));
            } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ResponseFactory.fail("驗證碼錯誤或已過期"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
            .body(ResponseFactory.fail("註冊過程中發生未知錯誤: " + e.getMessage()));
        }
    }
    

    @PostMapping("/sendVerifyEmail")
    public ResponseEntity<ApiRes<String>> sendVerifyEmail(
        @RequestBody VerifyEmailRequest request
    ) {

        ErrorCollector ec = new ErrorCollector();

        if(request == null) {
            ec.add("伺服器連線異常, 請稍後再嘗試");
        } else {
            ec.validate(request, validator);
        }

        UserMember foundUser = userMemberService.findUserByEmail(request.getEmail());
        if(foundUser != null) {
            ec.add("此信箱已經註冊過");
        }

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String subject = "請驗證您的電子郵件";
            EmailToken foundToken = tokenService.findTokenByEmail(email);
            if(foundToken != null) {
                tokenService.deleteByEmail(email);
            }

            String verificationCode = emailService.generateVerificationCode();
            emailService.saveVerificationToken(email, verificationCode);

            String emailContent = emailService.loadVerifyEmailTemplate(email, username, verificationCode);

            boolean isSent = emailService.sendMail(email, subject, emailContent, true);

            if (isSent) {
                return ResponseEntity.ok(ResponseFactory.success("驗證信已成功寄出至 " + email + "，請在註冊頁面輸入驗證碼"));
            } else {
                return ResponseEntity.internalServerError().body(ResponseFactory.fail("驗證信寄送失敗，請稍後再試"));
            }

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(ResponseFactory.fail("載入郵件模板時發生錯誤: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ResponseFactory.fail("寄送驗證信時發生未知錯誤: " + e.getMessage()));
        }
    }


    @PostMapping("/resendVerifyEmail")
    @Transactional
    public ResponseEntity<ApiRes<String>> resendVerifyEmail(
        @RequestBody ResendVerifyEmailRequest request // 你需要創建這個請求類
    ) {
        ErrorCollector ec = new ErrorCollector();

        if (request == null) {
            ec.add("伺服器連線異常，請稍後再嘗試");
        } else {
            ec.validate(request, validator); // 假設你也有針對 ResendVerifyEmailRequest 的驗證器
        }

        if (ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        String email = request.getEmail();

        try {
            boolean isResent = emailService.resendVerificationEmail(email);
            if (isResent) {
                return ResponseEntity.ok(ResponseFactory.success("驗證信已重新寄出至 " + email));
            } else {
                return ResponseEntity.internalServerError()
                    .body(ResponseFactory.fail("重新發送驗證信失敗，請稍後再試"));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(ResponseFactory.fail("重新發送驗證信時發生未知錯誤: " + e.getMessage()));
        }
    }




    @DeleteMapping
    @Transactional
    public ResponseEntity<String> deleteUserByEmail(
        @RequestBody UserEmail request
    ) {
        try {
            Integer res = userMemberService.deleteUserByEmail(request.getEmail());
            return ResponseEntity.ok("成功刪除帳號: " + res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("刪除帳號發生異常: " + e);
        }
    }








}


package tw.eeits.unhappy.ttpp.userMember.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import tw.eeits.unhappy.ttpp.userMember.dto.UserLoginRequest;
import tw.eeits.unhappy.ttpp.userMember.dto.UserRegisterRequest;
import tw.eeits.unhappy.ttpp.userMember.jwt.FrontJwtService;

@RestController
@RequestMapping("/api/user/secure")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserMemberService userMemberService; // 處理帳號密碼驗證
    private final FrontJwtService jwtService;
    private final Validator validator;
    private final EmailService emailService;

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

        if(request == null) {
            ec.add("註冊發生異常, 請稍後再嘗試");
        } else {
            ec.validate(request, validator);
        }

        // call service
        // register process
        ServiceResponse<UserMember> registerRes = userMemberService.register(request);

        if(!registerRes.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseFactory.fail(registerRes.getMessage()));
        }


        // auto login after register
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

        if(ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        try {
            String email = request.getEmail();
            String username = request.getUsername();
            String subject = "請驗證您的電子郵件"; // 設定郵件標題

            // 載入並客製化驗證信內容
            String emailContent = emailService.loadVerifyEmailTemplate(email, username);

            // 寄送驗證信
            boolean isSent = emailService.sendMail(email, subject, emailContent, true);

            if (isSent) {
                return ResponseEntity.ok(ResponseFactory.success("驗證信已成功寄出至 " + email));
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
    






}


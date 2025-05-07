package tw.eeits.unhappy.ttpp.userMember.controller;

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
import tw.eeits.unhappy.ttpp.userMember.dto.UserLoginRequest;
import tw.eeits.unhappy.ttpp.userMember.jwt.FrontJwtService;

@RestController
@RequestMapping("/api/user/secure")
@RequiredArgsConstructor
public class UserAuthController {

    private final UserMemberService userMemberService; // 處理帳號密碼驗證
    private final FrontJwtService jwtService;
    private final Validator validator;

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






}


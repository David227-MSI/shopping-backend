package tw.eeits.unhappy.ttpp.userMember.controller;

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
import tw.eeits.unhappy.ttpp.userMember.dto.UserModifyRequest;
import tw.eeits.unhappy.ttpp.userMember.jwt.FrontJwtService;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/user/secure")
@RequiredArgsConstructor
public class UserController {
    private final UserMemberService userMemberService;
    private final FrontJwtService jwtService;
    private final Validator validator;

    @PutMapping("/modify")
    public ResponseEntity<ApiRes<Map<String, Object>>> userModify(
        @RequestBody UserModifyRequest request
    ) {

        ErrorCollector ec = new ErrorCollector();
        ec.validate(request, validator);

        if (ec.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(ec.getErrorMessage()));
        }

        // call service
        ServiceResponse<UserMember> res = userMemberService.userModify(request);

        if (!res.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseFactory.fail(res.getMessage()));
        }

        UserMember updatedUser = res.getData();
        String token = jwtService.generateToken(updatedUser.getUsername(), updatedUser.getId()); // 生成新的 token

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userId", updatedUser.getId());
        data.put("username", updatedUser.getUsername());
        data.put("email", updatedUser.getEmail());
        data.put("phone", updatedUser.getPhone());
        data.put("address", updatedUser.getAddress());

        return ResponseEntity.ok(ResponseFactory.success(data));
    }









}

package tw.eeits.unhappy.eee.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.jwt.JsonWebTokenUtility;
import tw.eeits.unhappy.eee.service.LoginService;

@RestController
public class RegisterController {
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;
    
    @PostMapping("/ajax/secure/register")
    public String register(@RequestBody String body) {
        JSONObject responseBody = new JSONObject();
        
        try {
            JSONObject obj = new JSONObject(body);
            String email = obj.isNull("email") ? null : obj.getString("email");
            String password = obj.isNull("password") ? null : obj.getString("password");
            String username = obj.isNull("username") ? null : obj.getString("username");
            String birthdayStr = obj.isNull("birthday") ? null : obj.getString("birthday");
            String phone = obj.isNull("phone") ? null : obj.getString("phone");
            String fullAddress = obj.isNull("fullAddress") ? null : obj.getString("fullAddress");
            if(email == null || email.length()==0 ||
                password == null || password.length()==0 ||
               username == null || username.length()==0) {
                responseBody.put("success", false);
                responseBody.put("message", "請填寫必要資料");
                return responseBody.toString();
            }
            if (loginService.isEmailExists(email)) {
                responseBody.put("success", false);
                responseBody.put("message", "此帳號已被註冊");
                return responseBody.toString();
            }
            LocalDate birthday = null;
            if (birthdayStr != null && !birthdayStr.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                birthday = LocalDate.parse(birthdayStr, formatter);
            }
            UserMember customer = new UserMember();
            customer.setEmail(email);
            customer.setBirth(birthday);
            customer.setPhone(phone);
            customer.setAddress(fullAddress);
            customer.setUsername(username);
            loginService.registerCustomer(customer, password);
            String token = jsonWebTokenUtility.createToken(email);
            loginService.updateAccessToken(email, token);
            responseBody.put("success", true);
            responseBody.put("message", "註冊成功");
            responseBody.put("token", token);
            responseBody.put("email", email);
            responseBody.put("custid", customer.getId());
        } catch (Exception e) {
            responseBody.put("success", false);
            responseBody.put("message", "註冊失敗: " + e.getMessage());
        }        
        return responseBody.toString();
    }
}
package tw.eeits.unhappy.eee.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eee.jwt.JsonWebTokenUtility;
import tw.eeits.unhappy.eee.service.LoginService;
import tw.eeits.unhappy.eee.email.UserEmailService;

@RestController
public class ForgetPasswordController {
    
    @Autowired
    private LoginService loginService;
    
    @Autowired
    private UserEmailService emailService;
    
    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;
    @Value("${email.template.path:email_templates/forget_password_template.html}")
    private String emailTemplatePath;
    @PostMapping("/ajax/secure/forgetPassword")
    public String processForgetPassword(@RequestBody String body) {
        JSONObject responseBody = new JSONObject();
        JSONObject obj = new JSONObject(body);
        String email = obj.isNull("email01") ? null : obj.getString("email01");
        String verificationCode = obj.isNull("verificationCode") ? null : obj.getString("verificationCode");
        String newPassword = obj.isNull("password01") ? null : obj.getString("password01");
        String confirmPassword = obj.isNull("checkpassword") ? null : obj.getString("checkpassword");
        if(email == null || email.length()==0) {
            responseBody.put("success", false);
            responseBody.put("message", "請輸入電子郵件");
            return responseBody.toString();
        }
        UserMember customer = loginService.findByEmail(email);
        if (customer == null) {
            responseBody.put("success", false);
            responseBody.put("message", "帳號不存在");
            return responseBody.toString();
        }
        if(verificationCode == null || verificationCode.length()==0) {
            responseBody.put("success", false);
            responseBody.put("message", "請輸入驗證碼");
            return responseBody.toString();
        }
        
        if(customer.getEmailVerificationCode() == null || 
           !customer.getEmailVerificationCode().equals(verificationCode) || 
           customer.getVerificationCodeExpiresAt() == null || 
           customer.getVerificationCodeExpiresAt().before(new Date())) {
            
            responseBody.put("success", false);
            responseBody.put("message", "驗證碼無效或已過期，請重新獲取");
            return responseBody.toString();
        }
        
        if(newPassword == null || newPassword.length()==0 || confirmPassword == null || confirmPassword.length()==0) {
            responseBody.put("success", false);
            responseBody.put("message", "請輸入密碼和確認密碼");
            return responseBody.toString();
        }
        
        if(!newPassword.equals(confirmPassword)) {
            responseBody.put("success", false);
            responseBody.put("message", "密碼和確認密碼不一致");
            return responseBody.toString();
        }
        
        UserMember updatedCustomer = loginService.updatePassword(email, newPassword);
        if (updatedCustomer != null) {

            loginService.clearVerificationCode(email);
            String token = jsonWebTokenUtility.createToken(email);
            loginService.updateAccessToken(email, token);
            responseBody.put("success", true);
            responseBody.put("message", "密碼已成功更新，請登錄");
        } else {
            responseBody.put("success", false);
            responseBody.put("message", "密碼更新失敗，請重試");
        }
        
        return responseBody.toString();
    }
    

    @PostMapping("/ajax/secure/sendVerificationEmail")
    public String sendVerificationEmail(@RequestBody String body) {
        JSONObject responseBody = new JSONObject();
        

        JSONObject obj = new JSONObject(body);
        String email = obj.isNull("email") ? null : obj.getString("email");
        

        if(email == null || email.length()==0) {
            responseBody.put("success", false);
            responseBody.put("message", "請輸入電子郵件");
            return responseBody.toString();
        }
        
        UserMember customer = loginService.findByEmail(email);
        if (customer == null) {
            responseBody.put("success", false);
            responseBody.put("message", "帳號不存在");
            return responseBody.toString();
        }
        
        try {
            
            String verificationCode = generateVerificationCode();
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);
            Date expirationDate = Date.from(expiresAt.atZone(ZoneId.systemDefault()).toInstant());
            customer.setEmailVerificationCode(verificationCode);
            customer.setVerificationCodeExpiresAt(expirationDate);
            loginService.updateCustomer(customer);
            String subject = "密碼重設驗證";
            String message = "<p>親愛的用戶，您好：</p>" +
                           "<p>您最近請求重設密碼，您的驗證碼是：</p>" +
                           "<h2 style='color:#4A90E2; font-size:24px; text-align:center; padding:10px; background-color:#f5f5f5; border-radius:5px;'>" + verificationCode + "</h2>" +
                           "<p>此驗證碼將在5分鐘內有效。</p>" +
                           "<p>如果您沒有請求重設密碼，請忽略此郵件。</p>" +
                           "<p>謝謝！</p>";
            String emailContent = emailService.loadEmailTemplate(emailTemplatePath, subject, message);
            boolean sent = emailService.sendMail(email, subject, emailContent, true);
            if (sent) {
                responseBody.put("success", true);
                responseBody.put("message", "驗證碼已發送至您的信箱，請查收並在5分鐘內完成驗證");
            } else {
                responseBody.put("success", false);
                responseBody.put("message", "郵件發送失敗，請重試");
            }
        } catch (IOException e) {
            responseBody.put("success", false);
            responseBody.put("message", "系統錯誤：" + e.getMessage());
        }
        
        return responseBody.toString();
    }
    private String generateVerificationCode() {
        return String.format("%06d", (int)(Math.random() * 1000000));
    }
}
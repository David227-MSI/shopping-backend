package tw.eeits.unhappy.ttpp.email;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import tw.eeits.unhappy.ttpp.userMember.model.EmailToken;
import tw.eeits.unhappy.ttpp.userMember.repository.EmailTokenRepository;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailTokenRepository emailTokenRepository;
    private final ResourceLoader resourceLoader;
    private final Validator validator;

    private static final int VERIFICATION_CODE_LENGTH = 6; // 驗證碼長度


    public String loadResourceFile(String filePath) throws Exception {
        Resource resource = resourceLoader.getResource("classpath:" + filePath);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }







    // 讀取信件模板
    // 參數: 模板路徑, 標題, 收件人名稱, 內文
    public String loadEmailTemplate(String templatePath, String subject, String message) throws Exception {
        // load template
        String template = loadResourceFile(templatePath);
        
        // variables
        template = template.replace("${subject}", subject)
                           .replace("${message}", message);
        return template;
    }



    // default send text mail
    public boolean sendMail(String sendTo, String subject, String content) {
        return sendMail(sendTo, subject, content, false);
    }



    // 寄Email用這個
    // 參數: 收件信箱, 標題, 收件人名, 內文, (HTML/TEXT)
    public boolean sendMail(String sendTo, String subject, String text, boolean isHtml) {
        // check recipient address
        if (sendTo == null || sendTo == "") {
            System.err.println("收件信箱未填寫");
            return false;
        }

        if (isHtml == true) {
            // HTML mail
            try {
                MimeMessage msg = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setTo(sendTo);
                helper.setSubject(subject);
                helper.setText(text, true);
                javaMailSender.send(msg);
            } catch (Exception e) {
                System.err.println("Email寄送失敗: " + e.getMessage());
                return false;
            }

        } else {
            // text mail
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(sendTo);
            msg.setSubject(subject);
            msg.setText(text);
            try {
                javaMailSender.send(msg);
            } catch (Exception e) {
                System.err.println("Email寄送失敗: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public String loadVerifyEmailTemplate(
        String email,
        String username,
        String verificationCode
    ) throws Exception {

        // load template
        String templatePath = "static/email_templates/verifyEmailToken.html";
        String template = loadResourceFile(templatePath);

        // variables
        template = template.replace("${email}", email)
                        .replace("${username}", username)
                        .replace("${verificationCode}", verificationCode); // 添加驗證碼變數
        return template;
    }

    public String generateVerificationCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(VERIFICATION_CODE_LENGTH);
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            sb.append(random.nextInt(10)); // 生成 0-9 的隨機數字
        }
        return sb.toString();
    }

    public void saveVerificationToken(String email, String token) {
        EmailToken emailToken = new EmailToken();
        emailToken.setEmail(email);
        emailToken.setToken(token);
        emailToken.setExpire(LocalDateTime.now().plusMinutes(15)); // 設定 15 分鐘過期 (可調整)
        emailTokenRepository.save(emailToken);
    }
    
    public EmailToken findVerificationTokenByEmail(String email) {
        return emailTokenRepository.findByEmail(email).orElse(null);
    }
     
    public void updateVerificationToken(EmailToken existingToken, String newToken) {
        existingToken.setToken(newToken);
        existingToken.setExpire(LocalDateTime.now().plusMinutes(15)); // 重新設定過期時間
        emailTokenRepository.save(existingToken);
    }


    @Transactional
    public boolean verifyToken(String email, String token) {
        Optional<EmailToken> optionalToken = emailTokenRepository.findByEmail(email);

        if (optionalToken.isEmpty()) {return false;}

        EmailToken emailToken = optionalToken.get();

        if (emailToken.getExpire().isBefore(LocalDateTime.now())) {
            // Token 已過期，刪除記錄
            emailTokenRepository.delete(emailToken);
            return false;
        }

        if (emailToken.getToken().equals(token)) {
            // 驗證成功，刪除已使用的 Token
            emailTokenRepository.delete(emailToken);
            return true;
        } else {
            return false;
        }
    }



    @Transactional
    public boolean resendVerificationEmail(String email) {

        Optional<EmailToken> existingTokenOptional = emailTokenRepository.findByEmail(email);
        String newVerificationCode = generateVerificationCode();
        LocalDateTime newExpiryTime = LocalDateTime.now().plusMinutes(15); // 重新設定過期時間

        try {
            if (existingTokenOptional.isPresent()) {
                EmailToken existingToken = existingTokenOptional.get();
                existingToken.setToken(newVerificationCode);
                existingToken.setExpire(newExpiryTime);
                emailTokenRepository.save(existingToken);
            } else {
                // 如果沒有找到現有的 Token 記錄 (可能已過期並被刪除)，則創建一個新的
                EmailToken newToken = new EmailToken();
                newToken.setEmail(email);
                newToken.setToken(newVerificationCode);
                newToken.setExpire(newExpiryTime);
                emailTokenRepository.save(newToken);
            }

            String username = "親愛的用戶";
            String subject = "您的新驗證碼";
            String emailContent = loadVerifyEmailTemplate(email, username, newVerificationCode);
            return sendMail(email, subject, emailContent, true);

        } catch (Exception e) {
            System.err.println("重新發送驗證信時發生錯誤: " + e.getMessage());
            return false;
        }
    }




}

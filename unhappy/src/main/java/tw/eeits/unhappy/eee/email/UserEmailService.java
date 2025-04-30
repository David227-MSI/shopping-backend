package tw.eeits.unhappy.eee.email;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEmailService {
    private final JavaMailSender javaMailSender;
    public String loadEmailTemplate(String templatePath, String subject, String message) throws IOException {
        if (templatePath.startsWith("classpath:")) {
            templatePath = templatePath.substring("classpath:".length());
        }
        Resource resource = new ClassPathResource(templatePath);
        try (InputStream inputStream = resource.getInputStream()) {
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String template = new String(bytes, StandardCharsets.UTF_8);
            template = template.replace("${subject}", subject)
                               .replace("${message}", message);
            return template;
        }
    }
    public boolean sendMail(String sendTo, String subject, String text, boolean isHtml) {
        // check recipient address
        if (sendTo == null || sendTo.equals("")) {
            System.err.println("收件信箱未填寫");
            return false;
        }
        if (isHtml == true) {
            try {
                MimeMessage msg = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
                helper.setTo(sendTo);
                helper.setSubject(subject);
                helper.setText(text, true);
                javaMailSender.send(msg);
            } catch (Exception e) {
                System.err.println("Email寄送失敗: " + e.getMessage());
                e.printStackTrace(); 
                return false;
            }
        } else {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(sendTo);
            msg.setSubject(subject);
            msg.setText(text);
            try {
                javaMailSender.send(msg);
            } catch (Exception e) {
                System.err.println("Email寄送失敗: " + e.getMessage());
                e.printStackTrace(); 
                return false;
            }
        }
        return true;
    }
    public boolean sendMail(String sendTo, String subject, String content) {
        return sendMail(sendTo, subject, content, false);
    }
}
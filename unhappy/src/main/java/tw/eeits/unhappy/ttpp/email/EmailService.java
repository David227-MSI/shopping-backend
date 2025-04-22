package tw.eeits.unhappy.ttpp.email;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    // 讀取信件模板
    // 參數: 模板路徑, 標題, 收件人名稱, 內文
    public String loadEmailTemplate(String templatePath, String subject, String message) throws IOException {
        // load template
        String template = new String(Files.readAllBytes(Paths.get(templatePath)));
        
        // variables
        template = template.replace("${subject}", subject)
                           .replace("${message}", message);
        return template;
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
    // default send text mail
    public boolean sendMail(String sendTo, String subject, String content) {
        return sendMail(sendTo, subject, content, false);
    }

}

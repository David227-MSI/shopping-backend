package tw.eeits.unhappy.ttpp.emailTests;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import tw.eeits.unhappy.ttpp.email.EmailService;

@SpringBootTest
public class EmailTests {
    @Autowired
    private EmailService emailService;
    
    @Test
    public void testSendEmail() {
        String sendTo = "jfpoie13@gmail.com";
        String subject = "Test sending text mail";
        String text = "Hello, text mail";
        emailService.sendMail(sendTo, subject, text);
    }

    @Test
    public void testLoadEmailTemplate() {
        String path = "src/main/resources/static/email_templates/template1.html";
        String subject = "Test loading email template";
        String text = "Hello, email template";
        try {
            System.out.println(emailService.loadEmailTemplate(path, subject, text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendHtmlEmail() {
        String path = "src/main/resources/static/email_templates/template1.html";

        String sendTo = "jfpoie13@gmail.com";
        String subject = "Test sending text mail";
        String text = "Hello, text mail";

        try {
            String content = emailService.loadEmailTemplate(path, subject, text);
            emailService.sendMail(sendTo, subject, content, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

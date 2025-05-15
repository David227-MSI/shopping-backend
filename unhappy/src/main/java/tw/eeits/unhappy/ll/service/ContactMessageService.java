package tw.eeits.unhappy.ll.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tw.eeits.unhappy.ll.model.AdminUser;
import tw.eeits.unhappy.ll.model.ContactMessage;
import tw.eeits.unhappy.ll.repository.AdminUserRepository;
import tw.eeits.unhappy.ll.repository.ContactMessageRepository;
import tw.eeits.unhappy.ttpp.email.EmailService;

@Service
@Transactional
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private EmailService emailService;

    // 新增一筆訊息(未登入的使用者可以用)
    public ContactMessage createContactMessage(ContactMessage contactMessage) {
        contactMessage.setHandled(false); // 預設未處理
        return contactMessageRepository.save(contactMessage);
    }

    // 後台處理訊息
    public ContactMessage handleContactMessage(Integer id, String handlerUsername, boolean isHandled, String note, String replyMessage) {
        System.out.println("一開始收到的 handlerUsername: " + handlerUsername);
    
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到指定的聯絡訊息"));
    
        message.setHandled(isHandled);
        message.setHandledAt(isHandled ? LocalDateTime.now() : null);
    
        if (isHandled) {
            AdminUser adminUser = adminUserRepository.findByUsername(handlerUsername)
                    .orElseThrow(() -> new RuntimeException("找不到處理人員"));
            message.setHandledBy(adminUser);
    
            // ✅ 如果有輸入回應內容，就寄信給使用者
            if (replyMessage != null && !replyMessage.isBlank()) {
                String name = message.getName() != null ? message.getName().trim() : "親愛的顧客";
                String subject = String.format("【MyGaol買夠】親愛的 %s 您好，我們已處理您的聯絡訊息", name);
            
                emailService.sendMail(
                    message.getEmail(),
                    subject,
                    replyMessage
                );
            }
    
        } else {
            message.setHandledBy(null);
        }
    
        message.setNote(note);
    
        return contactMessageRepository.save(message);
    }
    

    // 取得未解決的訊息
    public List<ContactMessage> findAllUnhandledMessages() {
        return contactMessageRepository.findByIsHandledFalse();
    }

    // 取得所有訊息
    public List<ContactMessage> findAllMessages() {
        return contactMessageRepository.findAll();
    }
    

}

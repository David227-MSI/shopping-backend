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

@Service
@Transactional
public class ContactMessageService {

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    // 新增一筆訊息(未登入的使用者可以用)
    public ContactMessage createContactMessage(ContactMessage contactMessage) {
        contactMessage.setHandled(false); // 預設未處理
        return contactMessageRepository.save(contactMessage);
    }

    // 後台處理訊息
    public ContactMessage handleContactMessage(Integer id, String handlerUsername, boolean isHandled, String note) {
        System.out.println("一開始收到的 handlerUsername: " + handlerUsername);
        ContactMessage message = contactMessageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到指定的聯絡訊息"));

        message.setHandled(isHandled);
        message.setHandledAt(isHandled ? LocalDateTime.now() : null);

        if (isHandled) {
            AdminUser adminUser = adminUserRepository.findByUsername(handlerUsername)
                    .orElseThrow(() -> new RuntimeException("找不到處理人員"));
            // System.out.println("找到的adminUser: " + adminUser);
            message.setHandledBy(adminUser);
        } else {
            message.setHandledBy(null);
        }
        message.setNote(note);
        // System.out.println("存之前，處理人 handledBy = " + (message.getHandledBy() != null ?
        // message.getHandledBy().getUsername() : "null"));

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

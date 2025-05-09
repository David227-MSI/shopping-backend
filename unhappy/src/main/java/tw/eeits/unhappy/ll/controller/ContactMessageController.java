package tw.eeits.unhappy.ll.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import tw.eeits.unhappy.ll.dto.ContactMessageResponse;
import tw.eeits.unhappy.ll.dto.HandleContactMessageRequest;
import tw.eeits.unhappy.ll.model.ContactMessage;
import tw.eeits.unhappy.ll.service.ContactMessageService;

@RestController
@RequestMapping("")
public class ContactMessageController {

    @Autowired
    private ContactMessageService contactMessageService;

    // 新增一筆訊息(未登入的使用者可以用)
    @PostMapping("/api/public/contact")
    public ResponseEntity<?> createContactMessage(@Validated @RequestBody ContactMessage contactMessage) {
        ContactMessage saved = contactMessageService.createContactMessage(contactMessage);
        return ResponseEntity.ok(saved);
    }

    // 後台處理訊息

    @PutMapping("/api/admin/contact/{id}")
    public ResponseEntity<?> handleContactMessage(
            @PathVariable Integer id,
            @RequestBody @Validated HandleContactMessageRequest requestBody,
            HttpServletRequest request) {

        String handlerUsername = (String) request.getAttribute("username");

        ContactMessage updated = contactMessageService.handleContactMessage(
                id,
                handlerUsername,
                requestBody.getIsHandled(),
                requestBody.getNote());

        // 轉成DTO
        ContactMessageResponse response = ContactMessageResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .email(updated.getEmail())
                .subject(updated.getSubject())
                .message(updated.getMessage())
                .handled(updated.isHandled())
                .handledByUsername(updated.getHandledBy() != null ? updated.getHandledBy().getUsername() : null)
                .note(updated.getNote())
                .createdAt(updated.getCreatedAt())
                .handledAt(updated.getHandledAt())
                .build();

        return ResponseEntity.ok(response);
    }

    // 取得未解決的訊息
    @GetMapping("/api/admin/contact/unhandled")
    public ResponseEntity<?> getAllUnhandledMessages() {
        List<ContactMessage> messages = contactMessageService.findAllUnhandledMessages();
        return ResponseEntity.ok(messages);
    }

    // 取得所有訊息
    @GetMapping("/api/admin/contact")
    public ResponseEntity<?> getAllMessages() {
        List<ContactMessage> messages = contactMessageService.findAllMessages();

        // 轉成DTO List
        List<ContactMessageResponse> responseList = messages.stream()
                .map(m -> ContactMessageResponse.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .email(m.getEmail())
                        .subject(m.getSubject())
                        .message(m.getMessage())
                        .handled(m.isHandled())
                        .handledByUsername(m.getHandledBy() != null ? m.getHandledBy().getUsername() : null)
                        .note(m.getNote())
                        .createdAt(m.getCreatedAt())
                        .handledAt(m.getHandledAt())
                        .build())
                .toList();

        return ResponseEntity.ok(responseList);
    }

}

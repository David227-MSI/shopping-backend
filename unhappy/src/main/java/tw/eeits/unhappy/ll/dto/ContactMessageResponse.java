package tw.eeits.unhappy.ll.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContactMessageResponse {

    private Integer id;

    private String name;

    private String email;

    private String subject;

    private String message;

    private Boolean handled;

    private String handledByUsername; // 只回傳 username，不回傳整個 AdminUser物件

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime handledAt;
}

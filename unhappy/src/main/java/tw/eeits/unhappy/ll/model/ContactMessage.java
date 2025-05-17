package tw.eeits.unhappy.ll.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contact_message")
public class ContactMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @NotBlank(message = "撰寫者不可為空")
    @Column(name = "name", nullable = false)
    private String name;
    
    @NotBlank(message = "信箱不可為空")
    @Column(name = "email", nullable = false)
    private String email;

    @NotBlank(message = "主旨不可為空")
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotBlank(message = "訊息不可為空")
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "is_handled", nullable = false)
    private boolean isHandled;

    @ManyToOne
    @JoinColumn(name = "handled_by", referencedColumnName = "username")
    private AdminUser handledBy;
    
    @Column(name = "note")
    private String note;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "handled_at")
    private LocalDateTime handledAt;


}

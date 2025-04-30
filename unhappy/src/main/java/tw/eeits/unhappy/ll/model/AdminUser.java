package tw.eeits.unhappy.ll.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "admin_user")
public class AdminUser {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @NotBlank(message = "用戶名不可為空")
    @Column(name = "username", nullable = false)
    private String username;
    
    @NotBlank(message = "本名不可為空")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank(message = "職權不可為空")
    @Column(name = "role", nullable = false)
    private String role;

    @NotBlank(message = "密碼不可為空")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "狀態不可為空")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AdminUserStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "first_login_at")
    private LocalDateTime firstLoginAt;

}

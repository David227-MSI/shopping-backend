package tw.eeits.unhappy.ttpp.userMember.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
 @Table(name = "email_token")
 @Data
 @NoArgsConstructor
 @AllArgsConstructor
 public class EmailToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "電子郵件不能為空")
  @Email(message = "電子郵件格式不正確")
  @Column(unique = true, nullable = false)
  private String email;

  @NotBlank(message = "Token 不能為空")
  @Column(nullable = false)
  private String token;

  @Column(nullable = false)
  private LocalDateTime expire;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
   createdAt = LocalDateTime.now();
   updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
   updatedAt = LocalDateTime.now();
  }
 }

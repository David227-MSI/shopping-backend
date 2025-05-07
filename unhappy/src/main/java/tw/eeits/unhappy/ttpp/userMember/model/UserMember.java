// package tw.eeits.unhappy.ttpp.userMember.model;

// import java.time.LocalDate;
// import java.util.Date;

// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import jakarta.persistence.Temporal;
// import jakarta.persistence.TemporalType;
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Past;
// import jakarta.validation.constraints.Pattern;
// import jakarta.validation.constraints.Size;
// import lombok.Data;

// @Data
// @Entity
// @Table(name = "user_member")
// public class UserMember {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "id")
//     private Integer id;

//     @Email(message = "請提供有效的電子郵件")
//     @NotBlank(message = "電子郵件不能為空")
//     @Size(max = 100, message = "電子郵件長度不能超過100個字元")
//     @Column(name = "email", unique = true)
//     private String email;

//     @NotBlank(message = "密碼不能為空")
//     @Size(min = 8, max = 256, message = "密碼長度需為8~256字元")
//     @Column(name = "password")
//     private String password;

//     @NotBlank(message = "使用者名稱不能為空")
//     @Size(max = 50, message = "使用者名稱不能超過50個字元")
//     @Column(name = "user_name")
//     private String username;

//     @NotNull(message = "生日不能為空")
//     @Past(message = "生日必須是過去的日期")
//     @Column(name = "birthday")
//     private LocalDate birth;

//     @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
//     @Column(name = "phone", unique = true)
//     private String phone;

//     @Size(max = 255, message = "地址不能超過255個字元")
//     @Column(name = "address")
//     private String address;

//     @CreationTimestamp
//     @Temporal(TemporalType.TIMESTAMP)
//     @Column(name = "created_at", updatable = false)
//     private Date createdAt;

//     @UpdateTimestamp
//     @Temporal(TemporalType.TIMESTAMP)
//     @Column(name = "updated_at")
//     private Date updatedAt;
// }
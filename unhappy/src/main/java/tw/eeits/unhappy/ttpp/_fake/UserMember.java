// package tw.eeits.unhappy.ttpp._fake;

// import java.time.LocalDate;
// import java.time.LocalDateTime;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Data;
// import lombok.NoArgsConstructor;

// @Data
// @NoArgsConstructor
// @AllArgsConstructor
// @Builder
// @Entity
// @Table(name = "user_member")
// public class UserMember {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "id")
//     private Integer id;

//     @Column(name = "level_id")
//     private Integer levelId = 1;

//     @Column(name = "line_id", unique = true, length = 255)
//     private String lineId;

//     @Column(name = "google_id", unique = true, length = 255)
//     private String googleId;

//     @Column(name = "email", unique = true, length = 100)
//     private String email;

//     @Column(name = "password", nullable = false, length = 255)
//     private String password;

//     @Column(name = "user_name", nullable = false, length = 50)
//     private String userName;

//     @Column(name = "birthday", nullable = false)
//     private LocalDate birthday;

//     @Column(name = "phone", unique = true, length = 15)
//     private String phone;

//     @Column(name = "address", length = 255)
//     private String address;

//     @Column(name = "status", length = 10)
//     private String status = "active";

//     @Column(name = "carrier", length = 8)
//     private String carrier;

//     @Column(name = "bonus_point")
//     private Integer bonusPoint = 0;

//     @Column(name = "good_count")
//     private Integer goodCount = 0;

//     @Column(name = "bad_count")
//     private Integer badCount = 0;

//     @Column(name = "created_at")
//     private LocalDateTime createdAt = LocalDateTime.now();

//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt = LocalDateTime.now();

//     @Column(name = "refresh_token", length = Integer.MAX_VALUE)
//     private String refreshToken;

//     @Column(name = "token_expires_at")
//     private LocalDateTime tokenExpiresAt;


// }
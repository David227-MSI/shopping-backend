// package tw.eeits.unhappy.gy.domain;
// // TODO: 這是 stub 暫時版本，等 teammate 上傳正式版本後刪除

// import java.time.LocalDateTime;

// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.FetchType;
// import jakarta.persistence.Id;
// import jakarta.persistence.JoinColumn;
// import jakarta.persistence.ManyToOne;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.ToString;
// import tw.eeits.unhappy.eee.domain.UserMember;

// @Entity
// @Table(name = "coupon_published")
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// @Builder
// @ToString(exclude = {"userMember", "couponTemplate"})
// public class CouponPublished {

//     @Id
//     @Column(name = "id", nullable = false, length = 36)
//     private String id; // UUID 類型（char 36）

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "coupon_id")
//     private CouponTemplate couponTemplate;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "user_id")
//     private UserMember userMember;

//     @Column(name = "is_used")
//     private Boolean isUsed = false;

//     @CreationTimestamp
//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;

//     @UpdateTimestamp
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;

// }
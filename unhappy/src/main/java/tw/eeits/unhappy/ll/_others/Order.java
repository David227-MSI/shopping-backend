// package tw.eeits.unhappy.ll._others;

// import java.math.BigDecimal;
// import java.time.LocalDateTime;
// import java.util.List;

// import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

// import jakarta.persistence.CascadeType;
// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.EnumType;
// import jakarta.persistence.Enumerated;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.Table;
// import lombok.AllArgsConstructor;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.ToString;

// @Entity
// @Table(name = "orders")
// @Getter
// @Setter
// @AllArgsConstructor
// @NoArgsConstructor
// @Builder
// @ToString(exclude = {"userMember", "couponPublished", "orderItems"}) // 避免無窮迴圈
// public class Order {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Integer id;

//     // @ManyToOne(fetch = FetchType.LAZY)
//     // @JoinColumn(name = "user_id", nullable = false)
//     // private UserMember userMember;
//     @Column(name = "user_id", nullable = false)
//     private Integer userMember;

//     // @OneToOne(fetch = FetchType.LAZY)
//     // @JoinColumn(name = "coupon_published_id")
//     // private CouponPublished couponPublished;
//     @Column(name = "coupon_published_id", columnDefinition = "CHAR(36)")
//     private String couponPublished;

//     @Column(nullable = false)
//     private BigDecimal totalAmount;

//     private BigDecimal discountAmount;

//     @Enumerated(EnumType.STRING)
//     @Column(nullable = false, length = 50)
//     private OrderStatus status;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "payment_status", nullable = false, length = 50)
//     private PaymentStatus paymentStatus;

//     @Column(name = "payment_method", length = 50)
//     private String paymentMethod;

//     @Column(name = "paid_at")
//     private LocalDateTime paidAt;

//     @Column(name = "transaction_number", length = 100)
//     private String transactionNumber;

//     @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//     private List<OrderItem> orderItems;

//     @Column(name = "recipient_name")
//     private String recipientName;

//     @Column(name = "recipient_phone")
//     private String recipientPhone;

//     @Column(name = "recipient_address")
//     private String recipientAddress;

//     @CreationTimestamp
//     @Column(name = "created_at", updatable = false)
//     private LocalDateTime createdAt;

//     @UpdateTimestamp
//     @Column(name = "updated_at")
//     private LocalDateTime updatedAt;

// }

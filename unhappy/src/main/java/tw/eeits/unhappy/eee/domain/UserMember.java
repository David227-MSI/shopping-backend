package tw.eeits.unhappy.eee.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.gy.domain.CartItem;
import tw.eeits.unhappy.gy.domain.Order;
import tw.eeits.unhappy.ra.review.model.ProductReview;
import tw.eeits.unhappy.ttpp.coupon.model.CouponPublished;
import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
import tw.eeits.unhappy.ttpp.notification.model.SubscribeList;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_member")
public class UserMember {


    // mapped: fk_notification_published_user
    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationPublished> notificationPublished = new ArrayList<>();

    // mapped: fk_subscribe_list_user
    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscribeList> subscribeList = new ArrayList<>();

    // mapped: fk_coupon_published_user
    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CouponPublished> couponPublished = new ArrayList<>();

    // mapped: fk_review_user
    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductReview> productReviews = new ArrayList<>();

    // // mapped: FK_cart_user
    // @Builder.Default
    // @JsonIgnore
    // @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<CartItem> cartItems = new ArrayList<>();

    // // mapped: FK_order_user
    // @Builder.Default
    // @JsonIgnore
    // @OneToMany(mappedBy = "userMember", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Order> orders = new ArrayList<>();


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Email(message = "請提供有效的電子郵件")
    @NotBlank(message = "電子郵件不能為空")
    @Size(max = 100, message = "電子郵件長度不能超過100個字元")
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, max = 256, message = "密碼長度需為8~256字元")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "使用者名稱不能為空")
    @Size(max = 50, message = "使用者名稱不能超過50個字元")
    @Column(name = "user_name")
    private String username;

    @NotNull(message = "生日不能為空")
    @Past(message = "生日必須是過去的日期")
    @Column(name = "birthday")
    private LocalDate birth;

    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    @Column(name = "phone", unique = true)
    private String phone;

    @Size(max = 255, message = "地址不能超過255個字元")
    @Column(name = "address")
    private String address;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}

package tw.eeits.unhappy.gy.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import tw.eeits.unhappy.eee.domain.UserMember;
import tw.eeits.unhappy.eeit198product.entity.Product;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "product_id", "is_checked_out"})})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"userMember", "product"}) // 避免無窮迴圈
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserMember userMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "is_checked_out")
    private Boolean checkedOut = false;

    // 新增一筆資料時，created_at 會自動變成當前時間，updatable = false,之後 update 不會改這個欄位
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 每當資料列被更新時，自動填入當下的更新時間
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

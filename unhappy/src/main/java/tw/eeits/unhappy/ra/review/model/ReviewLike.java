package tw.eeits.unhappy.ra.review.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.eeits.unhappy.ra._fake.UserMember;

@Entity
@Table(name = "review_like", uniqueConstraints = @UniqueConstraint(columnNames = {"review_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "userMember 不可為空值")
    private UserMember userMember;
    // @NotNull(message = "User id 不可為空值")
    // @Column(name = "user_id", nullable = false)
    // private Integer userId;

    @NotNull(message = "Review id 不可為空值")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ProductReview productReview;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}


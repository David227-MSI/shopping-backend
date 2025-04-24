package tw.eeits.unhappy.ra.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.eeits.unhappy.ra.review.model.ReviewLike;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Integer> {
    
    /** 這篇 review 總按讚數 */
    long countByProductReviewId(Integer     reviewId);

    /** 判斷使用者是否已經按過讚（避免重複） */
    boolean existsByProductReviewIdAndUserId    (Integer reviewId,Integer userId);

}

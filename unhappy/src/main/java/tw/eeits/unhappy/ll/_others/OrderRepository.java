// package tw.eeits.unhappy.ll._others;

// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;

// @Repository
// public interface OrderRepository extends JpaRepository<Order, Integer> {
//     // 解決 LAZY loading (查明細)
//     @EntityGraph(attributePaths = "orderItems")
//     Optional<Order> findWithItemsById(@Param("id") Integer id);

//     // 查會員所有訂單 (依時間排序，從最新到最舊)
//     // List<Order> findByUserMember_IdOrderByCreatedAtDesc(Integer userId);
//     List<Order> findByUserMemberOrderByCreatedAtDesc(Integer userMemberId);

//     // 月銷報表要用的
//     @Query("SELECT o.id FROM Order o WHERE o.paymentStatus = 'PAID' AND o.createdAt >= :startDate AND o.createdAt < :endDate")
//     List<Integer> findPaidOrderIdsBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


// }

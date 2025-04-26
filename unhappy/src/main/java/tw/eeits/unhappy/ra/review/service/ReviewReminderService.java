// package tw.eeits.unhappy.ra.review.service;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;

// import tw.eeits.unhappy.ttpp._itf.NotificationService;
// import tw.eeits.unhappy.ttpp._response.ServiceResponse;
// import tw.eeits.unhappy.ttpp.notification.model.NotificationPublished;
// import tw.eeits.unhappy.ttpp.notification.model.NotificationTemplate;
// import tw.eeits.unhappy.ttpp.email.EmailService;

// /*
//     當訂單狀態變成 <code>OrderStatus.COMPLETED</code> 時，
//     由訂單端呼叫此 Service 立即提醒用戶「可以撰寫商品評論」。
//  */
// @Service
// @RequiredArgsConstructor
// public class ReviewReminderService {

//     private final NotificationService notificationService;
//     private final EmailService emailService;

//     /**
//      * 在訂單完成時呼叫，立即送出「撰寫評論」通知。
//      * @param userId       用戶 ID（必填）
//      * @param orderItemId  訂單明細 ID（用於前端定位對應的商品）
//      * @param userEmail    用戶 email；如不需寄信，可傳 <code>null</code>
//      */
//     public void remind(Integer userId, Integer orderItemId, String userEmail) {

//         /* ---------- 1. 站內通知 ---------- */
//         NotificationTemplate tpl = NotificationTemplate.builder()
//                 .title("訂單已完成，快來留下評論！")
//                 .content("點擊查看您的訂單並撰寫心得，分享給更多買家～")
//                 .build();

//         NotificationPublished np = NotificationPublished.builder()
//                 .userId(userId)
//                 .notificationTemplate(tpl)
//                 .isRead(false)
//                 .build();

//         ServiceResponse<NotificationPublished> resp =
//                 notificationService.publishNotification(np);

//         if (!resp.isSuccess()) {
//             System.err.println("推播通知失敗: " + resp.getMessage());
//         }

//         /* ---------- 2. Email 備援 ---------- */
//         if (userEmail != null && !userEmail.isBlank()) {
//             String subject = "感謝您的購買，歡迎留下評論！";
//             String body    = "您好！您的訂單已完成，現在就前往撰寫評論吧！";
//             emailService.sendMail(userEmail, subject, body);
//         }
//     }
// }

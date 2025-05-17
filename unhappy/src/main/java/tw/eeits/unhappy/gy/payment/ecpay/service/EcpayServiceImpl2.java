//package tw.eeits.unhappy.gy.payment.ecpay.service;
//
//import ecpay.payment.integration.AllInOne;
//import ecpay.payment.integration.domain.AioCheckOutOneTime;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import tw.eeits.unhappy.gy.domain.Order;
//import tw.eeits.unhappy.gy.dto.PaymentRequestDTO;
//import tw.eeits.unhappy.gy.enums.OrderStatus;
//import tw.eeits.unhappy.gy.enums.PaymentStatus;
//import tw.eeits.unhappy.gy.exception.OrderNotFoundException;
//import tw.eeits.unhappy.gy.order.repository.OrderRepository;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.UUID;
//
//@Service
//@Transactional
//public class EcpayServiceImpl2 implements EcpayService {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    private final AllInOne allInOne = new AllInOne("");
//
//    @Override
//    public String generateEcpayForm(PaymentRequestDTO paymentRequestDTO) {
//        // 查訂單，不存在丟自訂例外
//        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
//                .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));
//
//        // 產生 MerchantTradeNo
//        String merchantTradeNo = "SHOP" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
//
//        // 更新訂單 transactionNumber
//        order.setTransactionNumber(merchantTradeNo);
//        orderRepository.save(order);
//
//        // 建立 SDK 表單物件
//        AioCheckOutOneTime obj = new AioCheckOutOneTime();
//        obj.setMerchantTradeNo(merchantTradeNo);
//        obj.setMerchantTradeDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
//        obj.setTotalAmount(paymentRequestDTO.getAmount().setScale(0, BigDecimal.ROUND_HALF_UP).toString());
//        obj.setTradeDesc("Unhappy 購物網站付款");
//        obj.setItemName("Unhappy 訂單");
//        obj.setReturnURL("http://localhost:8080/api/ecpay/payment-callback"); // 後端接收通知
//        obj.setClientBackURL("http://localhost:5173/order-complete/" + order.getId());
//        obj.setNeedExtraPaidInfo("N");
//
//        return allInOne.aioCheckOut(obj, null); // 第二參數為發票參數，null 表示不開發票
//    }
//
//    @Override
//    public String handlePaymentCallback(HttpServletRequest request) {
//        String merchantTradeNo = request.getParameter("MerchantTradeNo");
//        String rtnCode = request.getParameter("RtnCode");
//
//        // 驗證回傳結果
//        if ("1".equals(rtnCode)) {
//            // 根據交易編號找到訂單
//            Order order = orderRepository.findByTransactionNumber(merchantTradeNo)
//                    .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));
//
//            // 更新訂單狀態
//            order.setStatus(OrderStatus.PAID);
//            order.setPaymentStatus(PaymentStatus.PAID);
//            order.setPaidAt(LocalDateTime.now());
//            orderRepository.save(order);
//
//            return "1|OK"; // 返回成功回應
//        } else {
//            return "0|FAIL"; // 返回錯誤訊息
//        }
//    }
//}

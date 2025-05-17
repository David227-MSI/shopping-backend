package tw.eeits.unhappy.gy.payment.ecpay.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.eeits.unhappy.gy.domain.Order;
import tw.eeits.unhappy.gy.dto.PaymentRequestDTO;
import tw.eeits.unhappy.gy.enums.OrderStatus;
import tw.eeits.unhappy.gy.enums.PaymentStatus;
import tw.eeits.unhappy.gy.exception.OrderNotFoundException;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;

import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@Transactional
public class EcpayServiceImpl implements EcpayService {

    @Autowired
    private OrderRepository orderRepository;

    @Value("${ecpay.return-url}")
    private String returnUrl;

    @Value("${ecpay.order-result-url}")
    private String orderResultUrl;


//    綠界參數 ( 下方可收簡訊 / 上方為無法接收簡訊 )

//    private final String HASH_KEY = "pwFHCqoQZGmho4w6";
//    private final String HASH_IV = "EkRm7iFT261dpevs";
//    private final String MERCHANT_ID = "3002607";
    private final String HASH_KEY = "5294y06JbISpM5x9";
    private final String HASH_IV = "v77hoKGq4KWxNNIS";
    private final String MERCHANT_ID = "2000132";

    @Override
    public Map<String, String> generateEcpayForm(PaymentRequestDTO dto) {
        // 查訂單
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));

        // 建立交易編號(綠界用)並更新
        String merchantTradeNo = "SHOP" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);
        order.setTransactionNumber(merchantTradeNo);
        orderRepository.save(order);

        // 建立參數
        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", MERCHANT_ID);
        params.put("MerchantTradeNo", merchantTradeNo);
        params.put("MerchantTradeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", dto.getAmount().setScale(0, RoundingMode.HALF_UP).toString());
        params.put("TradeDesc", URLEncoder.encode("Unhappy 購物網站付款", StandardCharsets.UTF_8));
        params.put("ItemName", "Unhappy 訂單");
        params.put("ReturnURL", returnUrl);
        params.put("OrderResultURL", orderResultUrl + "?orderId=" + order.getId());
        params.put("ChoosePayment", "Credit");
        params.put("NeedExtraPaidInfo", "N");

        // CheckMacValue
        String checkMacValue = generateCheckMacValue(params);
        params.put("CheckMacValue", checkMacValue);
        params.forEach((k, v) -> System.out.println(k + ": " + v));
        return params; // 回傳純欄位資料，讓前端動態組 <form>
    }

    private String generateCheckMacValue(Map<String, String> params) {
        Map<String, String> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder("HashKey=").append(HASH_KEY);
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        sb.append("&HashIV=").append(HASH_IV);

        try {
            String encoded = URLEncoder.encode(sb.toString(), "UTF-8")
                    .toLowerCase()
                    .replaceAll("%21", "!")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%2a", "*")
                    .replaceAll("%2d", "-")
                    .replaceAll("%2e", ".")
                    .replaceAll("%5f", "_");

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(encoded.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02X", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("CheckMacValue 產生失敗", e);
        }
    }

    @Override
    public String handlePaymentCallback(HttpServletRequest request) {
        String merchantTradeNo = request.getParameter("MerchantTradeNo");
        String rtnCode = request.getParameter("RtnCode");

        if ("1".equals(rtnCode)) {
            Order order = orderRepository.findByTransactionNumber(merchantTradeNo)
                    .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));

            order.setStatus(OrderStatus.COMPLETED);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);

            return "1|OK";
        } else {
            return "0|FAIL";
        }
    }
}

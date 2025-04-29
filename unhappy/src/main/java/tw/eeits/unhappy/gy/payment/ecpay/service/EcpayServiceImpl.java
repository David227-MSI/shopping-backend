package tw.eeits.unhappy.gy.payment.ecpay.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.eeits.unhappy.gy.domain.Order;
import tw.eeits.unhappy.gy.dto.PaymentRequestDTO;
import tw.eeits.unhappy.gy.enums.OrderStatus;
import tw.eeits.unhappy.gy.enums.PaymentStatus;
import tw.eeits.unhappy.gy.exception.OrderNotFoundException;
import tw.eeits.unhappy.gy.order.repository.OrderRepository;
import tw.eeits.unhappy.gy.payment.ecpay.config.EcpayProperties;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional
public class EcpayServiceImpl implements EcpayService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EcpayProperties ecpayProperties;

    @Override
    public String generateEcpayForm(PaymentRequestDTO paymentRequestDTO) {
        // 查訂單，不存在丟自訂例外
        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));

        // 產生 MerchantTradeNo
        String merchantTradeNo = "SHOP" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);

        // 更新訂單 transactionNumber
        order.setTransactionNumber(merchantTradeNo);
        orderRepository.save(order);

        // 建立綠界付款表單
        String merchantID = ecpayProperties.getMerchantId();
        String hashKey = ecpayProperties.getHashKey();
        String tradeDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        String totalAmount = paymentRequestDTO.getAmount().setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        String itemName = "Unhappy購物網站訂單";
        String returnURL = "http://localhost:8080/api/ecpay/payment-callback";  // 待正式域名

        // 計算 CheckMacValue
        String checkMacValue = generateCheckMacValue(merchantID, merchantTradeNo, tradeDate, totalAmount, itemName, returnURL, hashKey);

        // 產生付款表單
        String form = "<form id=\"ecpay-form\" action=\"https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5\" method=\"post\">" +
                "<input type=\"hidden\" name=\"MerchantID\" value=\"" + merchantID + "\">" +
                "<input type=\"hidden\" name=\"MerchantTradeNo\" value=\"" + merchantTradeNo + "\">" +
                "<input type=\"hidden\" name=\"MerchantTradeDate\" value=\"" + tradeDate + "\">" +
                "<input type=\"hidden\" name=\"PaymentType\" value=\"aio\">" +
                "<input type=\"hidden\" name=\"TotalAmount\" value=\"" + totalAmount + "\">" +
                "<input type=\"hidden\" name=\"TradeDesc\" value=\"購物網站付款\">" +
                "<input type=\"hidden\" name=\"ItemName\" value=\"" + itemName + "\">" +
                "<input type=\"hidden\" name=\"ReturnURL\" value=\"" + returnURL + "\">" +
                "<input type=\"hidden\" name=\"ChoosePayment\" value=\"Credit\">" +
                "<input type=\"hidden\" name=\"CheckMacValue\" value=\"" + checkMacValue + "\">" +
                "<input type=\"submit\" value=\"付款\">" +
                "</form>" +
                "<script>document.getElementById('ecpay-form').submit();</script>"; // 添加自動提交表單的腳本

        return form;
    }

    // 生成 CheckMacValue 的方法，傳入 hashKey 用來加密
    private String generateCheckMacValue(String merchantID, String merchantTradeNo, String tradeDate,
                                         String totalAmount, String itemName, String returnURL, String hashKey) {
        // 建立需要加密的原始資料串
        String rawValue = merchantID + "│" +
                merchantTradeNo + "│" +
                tradeDate + "│" +
                totalAmount + "│" +
                itemName + "│" +
                returnURL;

        // 計算 CheckMacValue，加入 hashKey 進行加密
        return md5Encrypt(rawValue + "│" + hashKey);
    }

    // 這裡使用 MD5 加密
    private String md5Encrypt(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                hexString.append(Integer.toHexString(0xFF & b));
            }
            return hexString.toString().toUpperCase();  // 返回大寫字母的 MD5 值
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 encryption failed", e);
        }
    }

    @Override
    public String handlePaymentCallback(HttpServletRequest request) {
        String merchantTradeNo = request.getParameter("MerchantTradeNo");
        String rtnCode = request.getParameter("RtnCode");

        // 驗證回傳結果
        if ("1".equals(rtnCode)) {
            // 根據交易編號找到訂單
            Order order = orderRepository.findByTransactionNumber(merchantTradeNo)
                    .orElseThrow(() -> new OrderNotFoundException("訂單不存在"));

            // 更新訂單狀態
            order.setStatus(OrderStatus.PAID);
            order.setPaymentStatus(PaymentStatus.PAID);
            order.setPaidAt(LocalDateTime.now());
            orderRepository.save(order);

            return "1|OK"; // 返回成功回應
        } else {
            return "0|FAIL"; // 返回錯誤訊息
        }
    }
}

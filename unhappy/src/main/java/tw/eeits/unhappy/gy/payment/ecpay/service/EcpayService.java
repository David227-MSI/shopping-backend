package tw.eeits.unhappy.gy.payment.ecpay.service;

import jakarta.servlet.http.HttpServletRequest;
import tw.eeits.unhappy.gy.dto.PaymentRequestDTO;

import java.util.Map;

public interface EcpayService {
    Map<String, String> generateEcpayForm(PaymentRequestDTO paymentRequestDTO);
    String handlePaymentCallback(HttpServletRequest request);
}

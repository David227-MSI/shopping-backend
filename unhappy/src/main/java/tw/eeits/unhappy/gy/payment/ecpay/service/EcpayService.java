package tw.eeits.unhappy.gy.payment.ecpay.service;

import jakarta.servlet.http.HttpServletRequest;
import tw.eeits.unhappy.gy.dto.PaymentRequestDTO;

public interface EcpayService {
    String generateEcpayForm(PaymentRequestDTO paymentRequestDTO);
    String handlePaymentCallback(HttpServletRequest request);
}

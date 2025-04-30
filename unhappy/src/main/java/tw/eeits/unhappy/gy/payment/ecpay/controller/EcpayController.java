package tw.eeits.unhappy.gy.payment.ecpay.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tw.eeits.unhappy.gy.dto.PaymentRequestDTO;
import tw.eeits.unhappy.gy.payment.ecpay.service.EcpayService;

@RestController
@RequestMapping("/api/ecpay")
public class EcpayController {

    @Autowired
    private EcpayService ecpayService;

    // 前端呼叫用: 產生綠界表單
    @PostMapping("/start-payment")
    public ResponseEntity<String> startPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        String paymentForm = ecpayService.generateEcpayForm(paymentRequestDTO);
        return ResponseEntity.ok(paymentForm);
    }

    // 綠界呼叫: 付款完成通知
    @PostMapping("/payment-callback")
    public ResponseEntity<String> paymentCallback(HttpServletRequest request) {
        String result = ecpayService.handlePaymentCallback(request);
        return ResponseEntity.ok(result);
    }
}

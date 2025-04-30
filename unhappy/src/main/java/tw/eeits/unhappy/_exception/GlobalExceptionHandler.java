//package tw.eeits.unhappy._exception;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import tw.eeits.unhappy.gy.dto.ErrorResponse;
//import tw.eeits.unhappy.gy.exception.*;
//
//import java.time.LocalDateTime;
//
//// 全域錯誤處理
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    // 建立錯誤回應(提出方法)
//    private ErrorResponse buildError(HttpStatus status, String message, HttpServletRequest request) {
//        return ErrorResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(status.value())
//                .message(message)
//                .path(request.getRequestURI())
//                .build();
//    }
//
//
//    //處理找不到使用者
//    @ExceptionHandler(UserNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
//    }
//
//
//    //找不到商品
//    @ExceptionHandler(ProductNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
//    }
//
//    //找不到購物車商品
//    @ExceptionHandler(CartItemNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
//    }
//
//    //處理數量異常
//    @ExceptionHandler(InvalidQuantityException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidQuantity(InvalidQuantityException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
//    }
//
//    //處理其餘雜項錯誤(兜底500錯誤)
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleOther(Exception ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request));
//    }
//
//    // 找不到優惠捲
//    @ExceptionHandler(CouponNotFoundException.class)
//    public ResponseEntity<ErrorResponse> handleCouponNotFound(CouponNotFoundException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
//    }
//
//    // 使用者購物車為空時拋出
//    @ExceptionHandler(EmptyCartException.class)
//    public ResponseEntity<ErrorResponse> handleCartEmpty(EmptyCartException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
//    }
//
//    // 自訂錯誤訊息，未達最低消費等情況
//    @ExceptionHandler(InvalidCouponUsageException.class)
//    public ResponseEntity<ErrorResponse> handleInvalidCouponUsage(InvalidCouponUsageException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
//    }
//
//    // 找不到訂單
//    @ExceptionHandler(OrderNotFoundException.class)
//    public  ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
//    }
//}

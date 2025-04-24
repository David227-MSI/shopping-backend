package tw.eeits.unhappy._exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tw.eeits.unhappy.ttpp._response.ApiRes;
import tw.eeits.unhappy.ttpp._response.ResponseFactory;

@RestControllerAdvice
public class GlobalErrorHandler {

    // 處理 JSON 格式錯誤 / Enum 轉換錯
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiRes<Object>> handleJsonParseError(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(
                ResponseFactory.fail("無效的輸入格式或參數錯誤：" + extractReason(ex))
        );
    }

    // 處理驗證錯誤（例如 @NotBlank, @NotNull）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiRes<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(ResponseFactory.fail("欄位驗證錯誤：" + errorMessage));
    }

    // 可以加個保險：兜底所有 RuntimeException
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiRes<Object>> handleOtherErrors(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseFactory.fail("系統發生錯誤：" + ex.getMessage()));
    }

    private String extractReason(HttpMessageNotReadableException ex) {
        Throwable root = ex.getRootCause() != null ? ex.getRootCause() : ex;
        return root.getMessage();
    }
}

package tw.eeits.unhappy.gy.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

//錯誤訊息統一格式
@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    //時間
    private LocalDateTime timestamp;
    //錯誤代碼
    private int status;
    //錯誤訊息
    private String message;
    //路徑
    private String path;
}

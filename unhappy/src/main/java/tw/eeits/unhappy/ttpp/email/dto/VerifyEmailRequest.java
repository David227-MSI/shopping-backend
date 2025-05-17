package tw.eeits.unhappy.ttpp.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyEmailRequest {

    @Email(message = "請提供有效的電子郵件")
    @NotBlank(message = "電子郵件為必要資訊")
    @Size(max = 100, message = "電子郵件長度不能超過100個字元")
    private String email;

    @NotBlank(message = "使用者名稱為必要資訊")
    @Size(max = 50, message = "使用者名稱不能超過50個字元")
    private String username;

}

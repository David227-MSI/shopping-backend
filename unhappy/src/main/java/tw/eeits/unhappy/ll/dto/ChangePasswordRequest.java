package tw.eeits.unhappy.ll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "新密碼不可為空")
    private String newPassword;

    // 若未來要支援舊密碼驗證，可加上這欄
    private String oldPassword;
}


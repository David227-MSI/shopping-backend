package tw.eeits.unhappy.ttpp.userMember.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserLoginRequest {
    @NotBlank(message = "請輸入Email")
    private String email;
    @NotBlank(message = "請輸入密碼")
    private String password;
}

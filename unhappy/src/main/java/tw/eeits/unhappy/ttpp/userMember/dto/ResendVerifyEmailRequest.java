package tw.eeits.unhappy.ttpp.userMember.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendVerifyEmailRequest {

 @NotBlank(message = "電子郵件不能為空")
 @Email(message = "請提供有效的電子郵件")
 private String email;
}

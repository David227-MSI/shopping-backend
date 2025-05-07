package tw.eeits.unhappy.ttpp.userMember.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserModifyRequest {
    private Integer id;

    @Email(message = "請提供有效的電子郵件")
    @Size(max = 100, message = "電子郵件長度不能超過100個字元")
    private String email;

    @Size(min = 8, max = 256, message = "密碼長度至少需要8碼")
    private String password;

    @Size(max = 50, message = "用戶名稱不能超過50個字元")
    private String username;

    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phone;

    @Size(max = 255, message = "地址過長")
    private String address;

    private String newPassword;

    private String oldPassword;

}

package tw.eeits.unhappy.ttpp.userMember.dto;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterRequest {

    @Email(message = "請提供有效的電子郵件")
    @NotBlank(message = "電子郵件為必要資訊")
    @Size(max = 100, message = "電子郵件長度不能超過100個字元")
    private String email;

    @NotBlank(message = "密碼為必要資訊")
    @Size(min = 8, max = 256, message = "密碼長度需為8~256字元")
    private String password;

    @NotBlank(message = "請再次輸入密碼確認")
    @Size(min = 8, max = 256, message = "密碼長度需為8~256字元")
    private String confirmPassword;

    @NotBlank(message = "使用者名稱為必要資訊")
    @Size(max = 50, message = "使用者名稱不能超過50個字元")
    private String username;

    @NotNull(message = "生日為必要資訊")
    @Past(message = "生日必須是過去的日期")
    @Column(name = "birthday")
    private LocalDate birth;

    @Pattern(regexp = "^09\\d{8}$", message = "手機號碼格式不正確")
    private String phone;

    @Size(max = 255, message = "地址不能超過255個字元")
    private String address;

    @NotBlank(message = "驗證碼為必要資訊")
    private String verificationCode;

    @AssertTrue(message = "請確認密碼輸入一致")
    private boolean checkConfirmPwd() {
        if (password != null && confirmPassword != null) {
            return password.equals(confirmPassword);
        }
        return false;
    }
}

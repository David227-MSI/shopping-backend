package tw.eeits.unhappy.ll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAdminUserRequest {

    @NotBlank(message = "帳號不可為空")
    private String username;

    @NotBlank(message = "本名不可為空")
    private String fullName;

    @NotBlank(message = "角色不可為空")
    private String role; // MANAGER / STAFF

    @NotBlank(message = "密碼不可為空")
    private String password;
}

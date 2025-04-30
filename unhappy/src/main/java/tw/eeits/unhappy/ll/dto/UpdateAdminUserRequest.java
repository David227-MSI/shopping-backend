package tw.eeits.unhappy.ll.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAdminUserRequest {

    @NotBlank(message = "角色不可為空")
    private String role; // manager 或 staff

    @NotBlank(message = "狀態不可為空")
    private String status; // ACTIVE / PENDING / SUSPENDED / INACTIVE
}
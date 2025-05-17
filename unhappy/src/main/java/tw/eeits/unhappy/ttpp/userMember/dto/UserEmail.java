package tw.eeits.unhappy.ttpp.userMember.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserEmail {
    @NotBlank(message = "不能輸入空值或空字串")
    private String email;
}

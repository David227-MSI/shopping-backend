package tw.eeits.unhappy.ll.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import tw.eeits.unhappy.ll.model.AdminUser;

@Data
@AllArgsConstructor
public class LoginResult {
    private AdminUser user;
    private boolean firstLogin;
}
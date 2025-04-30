package tw.eeits.unhappy.ll.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String username;
    private String role;
    private LocalDateTime firstLoginAt;
}


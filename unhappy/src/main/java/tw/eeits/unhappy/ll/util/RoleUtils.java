package tw.eeits.unhappy.ll.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

public class RoleUtils {
        public static void assertManager(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        if (!"MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "你沒有權限");
        }
    }
}

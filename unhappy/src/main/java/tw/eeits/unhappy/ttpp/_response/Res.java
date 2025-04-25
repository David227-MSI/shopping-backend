package tw.eeits.unhappy.ttpp._response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class Res {
    public static <T> ResponseEntity<ApiRes<T>> send(ApiRes<T> res) {
        return ResponseEntity
                .status(res.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(res);
    }
}


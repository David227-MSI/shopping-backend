package tw.eeits.unhappy.ttpp._response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ServiceResponse<T> success(T data) {
        return new ServiceResponse<>(true, "操作成功", data);
    }

    public static <T> ServiceResponse<T> success(String message, T data) {
        return new ServiceResponse<>(true, message, data);
    }

    public static <T> ServiceResponse<T> fail(String message) {
        return new ServiceResponse<>(false, message, null);
    }
}

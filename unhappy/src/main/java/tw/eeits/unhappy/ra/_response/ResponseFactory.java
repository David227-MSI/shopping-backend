package tw.eeits.unhappy.ra._response;

public class ResponseFactory {

    public static <T> ApiRes<T> success(T data) {
        return ApiRes.<T>builder()
                .success(true)
                .message("操作成功")
                .data(data)
                .build();
    }

    public static <T> ApiRes<T> success(String message, T data) {
        return ApiRes.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiRes<T> fail(String message) {
        return ApiRes.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();
    }
}


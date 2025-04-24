package tw.eeits.unhappy.ttpp._response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiRes<T> {

    private boolean success;
    private String message;
    private T data;
    
}

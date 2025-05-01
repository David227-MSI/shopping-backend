package tw.eeits.unhappy.ttpp.notification.dto;

import lombok.Data;

@Data
public class SubscribeQuery {
    private Integer userId;
    private Integer categoryId; 
    private Integer brandId; 
    private String search;
}

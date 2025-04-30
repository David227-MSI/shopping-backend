package tw.eeits.unhappy.ll.dto;

import java.time.LocalDateTime;

import lombok.Data;
import tw.eeits.unhappy.ll.model.BrandStatus;

@Data
public class BrandResponse {

    private Integer id;

    private String name;

    private String type;

    private String taxId;

    private String address;

    private String email;

    private String phone;

    private String fax;

    private String contactName;

    private String contactEmail;

    private String contactPhone;

    private BrandStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

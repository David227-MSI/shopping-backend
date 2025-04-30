package tw.eeits.unhappy.ll.dto;

import tw.eeits.unhappy.ll.model.Brand;

public class BrandDTO {
    private String name;
    private String statusText;

    public BrandDTO(Brand brand) {
        this.name = brand.getName();
        this.statusText = brand.getStatus().getLabel(); // 中文轉換
    }
}
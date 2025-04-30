package tw.eeits.unhappy.ra.media.dto;

import tw.eeits.unhappy.ra.media.model.ProductMedia;

public record ProductMediaDto(
    Integer id,
    String mediaType,
    String mediaUrl,
    String altText,
    Integer mediaOrder,
    Boolean isMain
) {
    public static ProductMediaDto from(ProductMedia entity) {
        return new ProductMediaDto(
            entity.getId(),
            entity.getMediaType().name(),
            entity.getMediaUrl(),
            entity.getAltText(),
            entity.getMediaOrder(),
            entity.getIsMain()
        );
    }
}

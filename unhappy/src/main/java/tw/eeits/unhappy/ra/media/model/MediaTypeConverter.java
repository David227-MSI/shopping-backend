package tw.eeits.unhappy.ra.media.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MediaTypeConverter implements AttributeConverter<MediaType,String>{
    @Override
    public String convertToDatabaseColumn(MediaType attr){
        return attr == null ? null : attr.name().toLowerCase();  // 存 image / video
    }
    @Override
    public MediaType convertToEntityAttribute(String db){
        return db == null ? null : MediaType.valueOf(db.toUpperCase());
    }
}
package tw.eeits.unhappy.ra.review.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
public class ReviewTagConverter implements AttributeConverter<Set<ReviewTag>, String> {

    @Override
    public String convertToDatabaseColumn(Set<ReviewTag> attribute) {
        if (attribute == null || attribute.isEmpty()) return "";
        return attribute.stream()
                        .map(ReviewTag::getLabel)  // 存中文
                        .collect(Collectors.joining(","));
    }

    @Override
    public Set<ReviewTag> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return Set.of();
        return Arrays.stream(dbData.split(","))
                    .map(String::trim)
                    .map(ReviewTag::fromLabel)  // 從中文找回 Enum
                    .collect(Collectors.toSet());
    }      
}
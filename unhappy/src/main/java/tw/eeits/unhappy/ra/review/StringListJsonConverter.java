package tw.eeits.unhappy.ra.review;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListJsonConverter implements AttributeConverter<List<String>, String> {

    private static final ObjectMapper om = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        try { return (list == null || list.isEmpty()) ? null : om.writeValueAsString(list); }
        catch (Exception e) { throw new IllegalArgumentException("序列化 reviewImages 失敗", e); }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        try { return (json == null || json.isBlank()) ? List.of()
                    : om.readValue(json, new TypeReference<>(){}); }
        catch (Exception e) { throw new IllegalArgumentException("反序列化 reviewImages 失敗", e); }
    }
}

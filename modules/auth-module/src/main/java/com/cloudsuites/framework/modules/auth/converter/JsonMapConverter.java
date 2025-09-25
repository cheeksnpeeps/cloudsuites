package com.cloudsuites.framework.modules.auth.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * JPA converter for Map<String, Object> to JSON string.
 * 
 * Converts Java Map objects to JSON strings for database storage
 * and back to Map objects when reading from the database.
 * 
 * @author CloudSuites Platform Team
 * @since 1.0.0
 */
@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(JsonMapConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE_REF = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        if (attribute == null) {
            return null;
        }
        
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting Map to JSON string", e);
            return "{}";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Map.of();
        }
        
        try {
            return objectMapper.readValue(dbData, MAP_TYPE_REF);
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON string to Map: {}", dbData, e);
            return Map.of();
        }
    }
}

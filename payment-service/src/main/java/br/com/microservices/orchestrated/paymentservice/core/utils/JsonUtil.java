package br.com.microservices.orchestrated.paymentservice.core.utils;

import br.com.microservices.orchestrated.paymentservice.config.exception.SerializationException;
import br.com.microservices.orchestrated.paymentservice.core.dto.EventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    /**
     * Serializes an object to a JSON string.
     *
     * @param object The object to be serialized.
     * @return A JSON string representing the object.
     * @throws SerializationException If an error occurs during serialization.
     * @throws IllegalArgumentException If the provided object is null.
     */
    public String toJson(Object object) throws SerializationException {
        if (object == null) {
            throw new IllegalArgumentException("The provided object is null.");
        }
        try{
            return objectMapper.writeValueAsString(object);
        }catch (JsonProcessingException e){
            log.error("Error in serializing object: ", e);
            throw new SerializationException("Failed to serialize object", e);

        }
    }

    /**
     * Deserializes a JSON string to an EventDto object.
     *
     * @param json The JSON string to be deserialized.
     * @return The EventDto object if deserialization is successful, or null if it fails.
     */
    public EventDto toEvent(String json) {
        if (json == null || json.trim().isEmpty()) {
            log.error("The provided JSON string is null or empty.");
            return null;
        }
        try {
            return objectMapper.readValue(json, EventDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error in deserializing the JSON string: ", e);
            return null;
        } catch (Exception e) {
            log.error("Unexpected error in deserialization: ", e);
            // Relançar se a exceção é crítica e não deve ser silenciada
            throw new RuntimeException("Unexpected error during deserialization", e);
        }
    }
}

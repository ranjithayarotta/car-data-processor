package org.example.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Car;
import org.example.exception.OutputFormatException;

import java.util.List;
import java.util.Objects;

public class JsonFormatter implements OutputFormatter {
    private final ObjectMapper objectMapper;

    public JsonFormatter() {
        this(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL));
    }

    public JsonFormatter(ObjectMapper customObjectMapper) {
        this.objectMapper = Objects.requireNonNull(customObjectMapper,
                "ObjectMapper cannot be null");
    }

    @Override
    public String format(List<Car> cars) {
        Objects.requireNonNull(cars, "Car list cannot be null");
        try {
            return objectMapper.writeValueAsString(cars);
        } catch (JsonProcessingException e) {
            throw new OutputFormatException(
                    String.format("Failed to generate JSON output for %d cars", cars.size()),
                    e);
        }
    }
}
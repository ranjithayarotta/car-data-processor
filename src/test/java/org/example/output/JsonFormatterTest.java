package org.example.output;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exception.OutputFormatException;
import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.output.JsonFormatter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonFormatterTest {

    private final JsonFormatter formatter = new JsonFormatter();

    @Test
    void testFormatValidCars() {
        Car car = new Car.Builder()
                .type("SUV")
                .brand("Toyota")
                .model("RAV4")
                .prices(Map.of("USD", new BigDecimal("30000")))
                .carBrand(new CarBrand.Builder()
                        .brand("Toyota")
                        .releaseDate(LocalDate.of(2022, 5, 10))
                        .build())
                .build();

        String json = formatter.format(List.of(car));
        assertNotNull(json);
        assertTrue(json.contains("Toyota"));
        assertTrue(json.contains("RAV4"));
        assertTrue(json.contains("USD"));
        assertTrue(json.contains("2022-05-10"));
    }

    @Test
    void testFormatEmptyList() {
        String json = formatter.format(List.of());
        assertEquals("[ ]", json.trim());
    }

    @Test
    void testFormatNullListThrowsException() {
        assertThrows(NullPointerException.class, () -> formatter.format(null));
    }

    @Test
    void testCustomObjectMapperThrowsOutputFormatException() {
        ObjectMapper badMapper = new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) throws JsonProcessingException {
                throw new JsonProcessingException("Simulated failure") {};
            }
        };
        JsonFormatter badFormatter = new JsonFormatter(badMapper);

        Car dummyCar = new Car.Builder()
                .type("SUV")
                .brand("Toyota")
                .model("RAV4")
                .prices(Collections.emptyMap())
                .build();

        OutputFormatException ex = assertThrows(OutputFormatException.class,
                () -> badFormatter.format(List.of(dummyCar)));

        assertTrue(ex.getMessage().contains("Failed to generate JSON output"));
    }
}

package org.example.output;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.output.TableFormatter;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TableFormatterTest {

    @Test
    void testFormatWithSingleCar() {
        TableFormatter formatter = new TableFormatter(Locale.US, "USD");

        Car car = new Car.Builder()
                .brand("Toyota")
                .model("RAV4")
                .type("SUV")
                .prices(Map.of("USD", new BigDecimal("30000")))
                .carBrand(new CarBrand.Builder()
                        .brand("Toyota")
                        .releaseDate(LocalDate.of(2022, 6, 15))
                        .build())
                .build();

        String output = formatter.format(List.of(car));

        assertNotNull(output);
        assertTrue(output.contains("Toyota"));
        assertTrue(output.contains("RAV4"));
        assertTrue(output.contains("SUV"));
        assertTrue(output.contains("$30,000.00"));
        assertTrue(output.contains("6/15/22") || output.matches(".*\\d{1,2}/\\d{1,2}/\\d{2}.*")); // Date format
    }

    @Test
    void testFormatEmptyList() {
        TableFormatter formatter = new TableFormatter();
        String output = formatter.format(List.of());
        assertEquals("No cars found.\n", output);
    }

    @Test
    void testFormatWithMissingPriceAndBrandDate() {
        TableFormatter formatter = new TableFormatter();

        Car car = new Car.Builder()
                .brand("Ford")
                .model("F-150")
                .type("TRUCK")
                .prices(Map.of())
                .carBrand(null)
                .build();

        String output = formatter.format(List.of(car));

        assertNotNull(output);
        assertTrue(output.contains("Ford"));
        assertTrue(output.contains("F-150"));
        assertTrue(output.contains("N/A"));
        assertTrue(output.contains("$0.00"));
    }

    @Test
    void testFormatWithLongModelNameTruncation() {
        TableFormatter formatter = new TableFormatter();
        String longModel = "SuperUltraMegaTurboRacingEdition12345";
        Car car = new Car.Builder()
                .brand("Honda")
                .model(longModel)
                .type("Coupe")
                .prices(Map.of("USD", new BigDecimal("50000")))
                .carBrand(new CarBrand.Builder()
                        .brand("Honda")
                        .releaseDate(LocalDate.of(2023, 1, 1))
                        .build())
                .build();

        String output = formatter.format(List.of(car));
        assertTrue(output.contains("Honda"));
        assertTrue(output.contains("SuperUltraMe..."));
    }

    @Test
    void testFormatNullListThrowsException() {
        TableFormatter formatter = new TableFormatter();
        assertThrows(NullPointerException.class, () -> formatter.format(null));
    }

    @Test
    void testConstructorRejectsNullLocale() {
        assertThrows(NullPointerException.class, () -> new TableFormatter(null, "USD"));
    }

    @Test
    void testConstructorRejectsBlankCurrencyCode() {
        assertThrows(IllegalArgumentException.class, () -> new TableFormatter(Locale.US, " "));
    }
}

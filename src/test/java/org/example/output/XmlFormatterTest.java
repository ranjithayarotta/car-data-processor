package org.example.output;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.output.XmlFormatter;
import org.example.output.XmlFormatter.SimpleCar;
import org.example.output.XmlFormatter.CarListWrapper;
import org.example.exception.OutputFormatException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XmlFormatterTest {

    @Test
    void testFormatWithValidCar() {
        XmlFormatter formatter = new XmlFormatter();

        Car car = new Car.Builder()
                .brand("Toyota")
                .model("Corolla")
                .type("Sedan")
                .prices(Map.of("USD", new BigDecimal("25000")))
                .carBrand(new CarBrand.Builder()
                        .brand("Toyota")
                        .releaseDate(LocalDate.of(2020, 5, 20))
                        .build())
                .build();

        String xml = formatter.format(List.of(car));

        assertNotNull(xml);
        assertTrue(xml.contains("<car>"));
        assertTrue(xml.contains("<brand>Toyota</brand>"));
        assertTrue(xml.contains("<model>Corolla</model>"));
        assertTrue(xml.contains("<type>Sedan</type>"));
        assertTrue(xml.contains("<price>$25,000.00</price>"));
        assertTrue(xml.contains("<releaseDate>05/20/2020</releaseDate>"));
    }

    @Test
    void testFormatWithMissingFields() {
        XmlFormatter formatter = new XmlFormatter();

        Car car = new Car.Builder()
                .brand(null)
                .model(null)
                .type(null)
                .prices(Collections.emptyMap())
                .carBrand(null)
                .build();

        String xml = formatter.format(List.of(car));

        assertNotNull(xml);
        assertTrue(xml.contains("<brand>N/A</brand>"));
        assertTrue(xml.contains("<model>N/A</model>"));
        assertTrue(xml.contains("<type>N/A</type>"));
        assertTrue(xml.contains("<price>$0.00</price>"));
        assertTrue(xml.contains("<releaseDate>N/A</releaseDate>"));
    }

    @Test
    void testFormatWithEmptyList() {
        XmlFormatter formatter = new XmlFormatter();
        String xml = formatter.format(List.of());

        assertNotNull(xml);
        assertTrue(xml.contains("<cars/>") || xml.contains("<cars></cars>"));
    }

    @Test
    void testFormatWithNullList() {
        XmlFormatter formatter = new XmlFormatter();
        String xml = formatter.format(null);
        assertNotNull(xml);
        assertTrue(xml.contains("<cars/>") || xml.contains("<cars></cars>"));
    }

    @Test
    void testPriceAdapterMarshalling() throws Exception {
        XmlFormatter.PriceAdapter adapter = new XmlFormatter.PriceAdapter();

        String result = adapter.marshal(new BigDecimal("1234.56"));
        assertEquals("$1,234.56", result);

        BigDecimal unmarshalled = adapter.unmarshal("$1,234.56");
        assertEquals(new BigDecimal("1234.56"), unmarshalled);
    }

    @Test
    void testInitializationFailureHandling() {
        assertDoesNotThrow(XmlFormatter::new);
    }

    @Test
    void testXmlFormatterHandlesValidInput() {
        XmlFormatter formatter = new XmlFormatter();

        List<Car> cars = List.of(
                new Car.Builder()
                        .brand("FailBrand")
                        .model("FailModel")
                        .type("FailType")
                        .prices(Map.of("USD", new BigDecimal("100")))
                        .carBrand(new CarBrand.Builder()
                                .brand("FailBrand")
                                .releaseDate(LocalDate.of(2023, 1, 1))
                                .build())
                        .build()
        );

        String output = formatter.format(cars);
        assertTrue(output.contains("FailBrand"));
    }
}

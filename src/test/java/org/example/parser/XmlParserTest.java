package org.example.parser;

import org.example.model.Car;
import org.example.parser.XmlParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    private final XmlParser parser = new XmlParser();
    private File tempFile;

    @AfterEach
    void cleanup() throws IOException {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    private File createTempXmlFile(String content) throws IOException {
        tempFile = File.createTempFile("test-cars", ".xml");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(content);
        }
        return tempFile;
    }

    @Test
    void testParseValidCars() throws Exception {
        String xml = """
                <cars>
                    <car>
                        <type>SUV</type>
                        <model>RAV4</model>
                        <price currency="USD">30000</price>
                        <prices>
                            <price currency="EUR">28000</price>
                        </prices>
                    </car>
                    <car>
                        <type>SEDAN</type>
                        <model>Civic</model>
                        <price currency="USD">22000</price>
                    </car>
                </cars>
                """;

        File file = createTempXmlFile(xml);
        List<Car> cars = parser.parse(file);

        assertEquals(2, cars.size());

        Car car1 = cars.get(0);
        assertEquals("Toyota", car1.getBrand());
        assertEquals("RAV4", car1.getModel());
        assertEquals(new BigDecimal("30000"), car1.getPrices().get("USD"));
        assertEquals(new BigDecimal("28000"), car1.getPrices().get("EUR"));

        Car car2 = cars.get(1);
        assertEquals("Honda", car2.getBrand());
        assertEquals("Civic", car2.getModel());
    }

    @Test
    void testParseInvalidPriceIgnored() throws Exception {
        String xml = """
                <cars>
                    <car>
                        <type>SUV</type>
                        <model>RAV4</model>
                        <price currency="USD">invalid</price>
                    </car>
                </cars>
                """;

        File file = createTempXmlFile(xml);
        List<Car> cars = parser.parse(file);

        assertEquals(1, cars.size());
        assertTrue(cars.get(0).getPrices().isEmpty());
    }

    @Test
    void testUnknownModelDefaultsToUnknownBrand() throws Exception {
        String xml = """
                <cars>
                    <car>
                        <type>COUPE</type>
                        <model>UnknownModel</model>
                        <price currency="USD">10000</price>
                    </car>
                </cars>
                """;

        File file = createTempXmlFile(xml);
        List<Car> cars = parser.parse(file);

        assertEquals(1, cars.size());
        assertEquals("Unknown", cars.get(0).getBrand());
    }

    @Test
    void testTeslaModelName() throws Exception {
        String xml = """
                <cars>
                    <car>
                        <type>SEDAN</type>
                        <model>Model 3</model>
                        <price currency="USD">35000</price>
                    </car>
                </cars>
                """;

        File file = createTempXmlFile(xml);
        List<Car> cars = parser.parse(file);

        assertEquals(1, cars.size());
        assertEquals("Tesla", cars.get(0).getBrand());
    }

    @Test
    void testMercedesModelName() throws Exception {
        String xml = """
                <cars>
                    <car>
                        <type>LUXURY</type>
                        <model>C-200</model>
                        <price currency="USD">45000</price>
                    </car>
                </cars>
                """;

        File file = createTempXmlFile(xml);
        List<Car> cars = parser.parse(file);

        assertEquals(1, cars.size());
        assertEquals("Mercedes-Benz", cars.get(0).getBrand());
    }

    @Test
    void testMalformedXmlThrowsException() {
        String invalidXml = "<cars><car><type>SUV</type></cars>";

        assertThrows(XmlParser.ParserException.class, () -> {
            File file = createTempXmlFile(invalidXml);
            parser.parse(file);
        });
    }

    @Test
    void testNullFileThrowsException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    void testEmptyFileReturnsEmptyList() throws Exception {
        File file = createTempXmlFile("<cars></cars>");
        List<Car> cars = parser.parse(file);

        assertTrue(cars.isEmpty());
    }
}

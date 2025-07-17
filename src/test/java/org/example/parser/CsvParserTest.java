package org.example.parser;

import org.example.model.CarBrand;
import org.example.parser.CsvParser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvParserTest {

    private final CsvParser parser = new CsvParser();
    private File tempFile;

    private File createTempCsv(String content) throws Exception {
        tempFile = File.createTempFile("test-brands", ".csv");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(content);
        }
        return tempFile;
    }

    @AfterEach
    void tearDown() throws Exception {
        if (tempFile != null && tempFile.exists()) {
            Files.delete(tempFile.toPath());
        }
    }

    @Test
    void testValidCsvFile() throws Exception {
        String csv = """
                Brand,ReleaseDate
                Toyota,01/01/2020
                Ford,12/15/2021
                """;

        File file = createTempCsv(csv);
        List<CarBrand> result = parser.parse(file);

        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
        assertEquals(LocalDate.of(2020, 1, 1), result.get(0).getReleaseDate());
        assertEquals("Ford", result.get(1).getBrand());
    }

    @Test
    void testInvalidDateFormat_skipsLine() throws Exception {
        String csv = """
                Brand,ReleaseDate
                Toyota,01/01/2020
                Ford,INVALID_DATE
                """;

        File file = createTempCsv(csv);
        List<CarBrand> result = parser.parse(file);

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void testMalformedLine_skipsLine() throws Exception {
        String csv = """
                Brand,ReleaseDate
                Toyota,01/01/2020
                MalformedLineWithoutComma
                """;

        File file = createTempCsv(csv);
        List<CarBrand> result = parser.parse(file);

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void testEmptyFileReturnsEmptyList() throws Exception {
        File file = createTempCsv("Brand,ReleaseDate\n");
        List<CarBrand> result = parser.parse(file);

        assertTrue(result.isEmpty());
    }

    @Test
    void testNullFileThrowsException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    void testFileDoesNotExistThrowsParserException() {
        File file = new File("non_existent_file.csv");
        CsvParser.ParserException exception = assertThrows(
                CsvParser.ParserException.class,
                () -> parser.parse(file)
        );
        assertTrue(exception.getMessage().contains("Failed to read CSV file"));
    }
}

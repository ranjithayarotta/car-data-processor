package org.example.parser;

import org.example.model.CarBrand;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvParser implements FileParser<CarBrand> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final int EXPECTED_COLUMNS = 2;
    private static final int BRAND_INDEX = 0;
    private static final int DATE_INDEX = 1;

    @Override
    public List<CarBrand> parse(File file) throws ParserException {
        Objects.requireNonNull(file, "Input file cannot be null");

        try (Stream<String> lines = Files.lines(file.toPath())) {
            return lines
                    .skip(1) // Skip header
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            throw new ParserException("Failed to read CSV file: " + file.getName(), e);
        }
    }

    private CarBrand parseLine(String line) {
        try {
            String[] parts = line.replace("\"", "").split(",");
            if (parts.length != EXPECTED_COLUMNS) {
                System.err.println("Skipping malformed line: " + line);
                return null;
            }

            String brandName = parts[BRAND_INDEX].trim();
            LocalDate releaseDate = parseDate(parts[DATE_INDEX].trim());

            return new CarBrand.Builder()
                    .brand(brandName)
                    .releaseDate(releaseDate)
                    .build();
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format in line: " + line);
            return null;
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line + " - " + e.getMessage());
            return null;
        }
    }

    private LocalDate parseDate(String dateString) throws DateTimeParseException {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }

    public static class ParserException extends Exception {
        public ParserException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
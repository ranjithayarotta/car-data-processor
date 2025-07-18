package org.example.output;

import org.example.model.Car;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class TableFormatter implements OutputFormatter {
    private static final int BRAND_WIDTH = 15;
    private static final int MODEL_WIDTH = 15;
    private static final int TYPE_WIDTH = 20;
    private static final int PRICE_WIDTH = 15;
    private static final int DATE_WIDTH = 15;

    private static final String HEADER_FORMAT =
            "%-" + BRAND_WIDTH + "s %-" + MODEL_WIDTH + "s %-" + TYPE_WIDTH + "s %" +
                    PRICE_WIDTH + "s %" + DATE_WIDTH + "s\n";
    private static final String ROW_FORMAT = HEADER_FORMAT;
    private static final String DIVIDER =
            new String(new char[BRAND_WIDTH + MODEL_WIDTH + TYPE_WIDTH + PRICE_WIDTH + DATE_WIDTH + 4])
                    .replace("\0", "-") + "\n";


    private final Locale locale;
    private final String currencyCode;
    private final DateTimeFormatter dateFormatter;
    private final NumberFormat currencyFormat;

    public TableFormatter() {
        this(Locale.US, "USD"); // Default to US locale and USD
    }

    public TableFormatter(Locale locale, String currencyCode) {
        this.locale = Objects.requireNonNull(locale);
        this.currencyCode = validateCurrency(currencyCode);
        this.dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(locale);
        this.currencyFormat = createCurrencyFormatter();
    }

    @Override
    public String format(List<Car> cars) {
        Objects.requireNonNull(cars);

        if (cars.isEmpty()) {
            return "No cars found.\n";
        }

        StringBuilder sb = new StringBuilder();
        appendHeader(sb);
        appendDivider(sb);
        appendRows(sb, cars);
        return sb.toString();
    }

    private void appendHeader(StringBuilder sb) {
        sb.append(String.format(HEADER_FORMAT,
                "Brand", "Model", "Type",
                currencyCode + " Price",
                "Release Date"));
    }

    private void appendDivider(StringBuilder sb) {
        sb.append(DIVIDER);
    }

    private void appendRows(StringBuilder sb, List<Car> cars) {
        cars.forEach(car -> appendRow(sb, car));
    }

    private void appendRow(StringBuilder sb, Car car) {
        try {
            String brand = formatField(car.getBrand(), BRAND_WIDTH);
            String model = formatField(car.getModel(), MODEL_WIDTH);
            String type = formatField(car.getType(), TYPE_WIDTH);
            String price = formatPrice(car);
            String date = formatDate(car);

            sb.append(String.format(ROW_FORMAT, brand, model, type, price, date));
        } catch (Exception e) {
            appendErrorRow(sb, car);
        }
    }

    private String formatPrice(Car car) {
        BigDecimal price = car.getPrices().getOrDefault(currencyCode, BigDecimal.ZERO);
        String formatted = currencyFormat.format(price);
        return String.format("%" + PRICE_WIDTH + "s", formatted);
    }

    private String formatDate(Car car) {
        return Optional.ofNullable(car.getCarBrand())
                .flatMap(brand -> Optional.ofNullable(brand.getReleaseDate()))
                .map(date -> date.format(dateFormatter))
                .map(date -> String.format("%" + DATE_WIDTH + "s", date))
                .orElse(String.format("%" + DATE_WIDTH + "s", "N/A"));
    }

    private String formatField(String value, int width) {
        if (value == null || value.isEmpty()) {
            return String.format("%-" + width + "s", "N/A");
        }
        return String.format("%-" + width + "s",
                value.length() > width ? value.substring(0, width - 3) + "..." : value);
    }

    private void appendErrorRow(StringBuilder sb, Car car) {
        String brand = formatField(car != null ? car.getBrand() : "UNKNOWN", BRAND_WIDTH);
        String model = formatField(car != null ? car.getModel() : "UNKNOWN", MODEL_WIDTH);
        sb.append(String.format(ROW_FORMAT,
                brand, model,
                formatField("ERROR", TYPE_WIDTH),
                formatField("ERROR", PRICE_WIDTH),
                formatField("ERROR", DATE_WIDTH)));
    }

    // Helper methods
    private String validateCurrency(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency code cannot be empty");
        }
        return code.trim().toUpperCase();
    }

    private NumberFormat createCurrencyFormatter() {
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        fmt.setMaximumFractionDigits(2);
        fmt.setMinimumFractionDigits(2);
        return fmt;
    }
}
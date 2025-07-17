package org.example.strategy;

import org.example.model.Car;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public class BrandPriceFilterStrategy implements FilterStrategy {
    private static final String DEFAULT_CURRENCY = "USD";
    private static final BigDecimal DEFAULT_MIN_PRICE = BigDecimal.ZERO;
    private static final BigDecimal DEFAULT_MAX_PRICE = new BigDecimal(Long.MAX_VALUE);

    private final String brand;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final String currencyCode;

    public BrandPriceFilterStrategy(String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        this(brand, minPrice, maxPrice, DEFAULT_CURRENCY);
    }

    public BrandPriceFilterStrategy(String brand, BigDecimal minPrice,
                                    BigDecimal maxPrice, String currencyCode) {
        this.brand = Objects.requireNonNull(brand, "Brand cannot be null");
        this.minPrice = Optional.ofNullable(minPrice).orElse(DEFAULT_MIN_PRICE);
        this.maxPrice = Optional.ofNullable(maxPrice).orElse(DEFAULT_MAX_PRICE);
        this.currencyCode = Objects.requireNonNull(currencyCode, "Currency code cannot be null");

        validatePriceRange();
    }

    @Override
    public boolean filter(Car car) {
        if (car == null) {
            return false;
        }

        return matchesBrand(car) && matchesPriceRange(car);
    }

    private boolean matchesBrand(Car car) {
        return brand.equalsIgnoreCase(car.getBrand());
    }

    private boolean matchesPriceRange(Car car) {
        try {
            BigDecimal price = Optional.ofNullable(car.getPrices())
                    .map(prices -> prices.get(currencyCode))
                    .orElse(null);

            return price != null &&
                    price.compareTo(minPrice) >= 0 &&
                    price.compareTo(maxPrice) <= 0;
        } catch (Exception e) {
            return false; // Safe fallback for any errors
        }
    }

    private void validatePriceRange() {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException(
                    "Min price cannot be greater than max price");
        }
    }
}
package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

public class BrandDateFilterStrategy implements FilterStrategy {
    private final String brand;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BrandRepository brandRepository;

    public BrandDateFilterStrategy(String brand, LocalDate startDate,
                                   LocalDate endDate, BrandRepository brandRepository) {
        this.brand = Objects.requireNonNull(brand, "Brand cannot be null");
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null");
        this.brandRepository = Objects.requireNonNull(brandRepository, "BrandRepository cannot be null");

        validateDateRange();
    }

    @Override
    public boolean filter(Car car) {
        if (car == null || !matchesBrand(car)) {
            return false;
        }
        return getReleaseDate(car)
                .filter(this::isWithinDateRange)
                .isPresent();
    }

    private boolean matchesBrand(Car car) {
        return brand.equalsIgnoreCase(car.getBrand());
    }

    private Optional<LocalDate> getReleaseDate(Car car) {
        try {
            return brandRepository.findByBrand(car.getBrand())
                    .map(CarBrand::getReleaseDate);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private boolean isWithinDateRange(LocalDate releaseDate) {
        return releaseDate != null &&
                !releaseDate.isBefore(startDate) &&
                !releaseDate.isAfter(endDate);
    }

    private void validateDateRange() {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }
    }
}
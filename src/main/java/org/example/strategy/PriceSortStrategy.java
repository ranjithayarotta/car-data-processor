package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PriceSortStrategy implements SortStrategy {
    private static final String DEFAULT_CURRENCY = "USD";
    private static final BigDecimal DEFAULT_PRICE = BigDecimal.ZERO;
    private static final Comparator<BigDecimal> PRICE_COMPARATOR =
            Comparator.nullsLast(Comparator.reverseOrder()); // Highest to lowest

    private final String currencyCode;
    private final BrandRepository brandRepository;

    public PriceSortStrategy(BrandRepository brandRepository) {
        this(DEFAULT_CURRENCY, brandRepository);
    }

    public PriceSortStrategy(String currencyCode, BrandRepository brandRepository) {
        this.currencyCode = Objects.requireNonNull(currencyCode,
                "Currency code cannot be null");
        this.brandRepository = Objects.requireNonNull(brandRepository,
                "BrandRepository cannot be null");
    }

    @Override
    public List<Car> sort(List<Car> cars) {
        if (cars == null || cars.isEmpty()) {
            return List.of();
        }

        Map<String, CarBrand> brandCache = fetchBrandsForCars(cars);

        return cars.stream()
                .filter(Objects::nonNull)
                .map(car -> enrichCarWithBrand(car, brandCache))
                .sorted(createPriceComparator())
                .collect(Collectors.toList());
    }

    private Map<String, CarBrand> fetchBrandsForCars(List<Car> cars) {
        Set<String> neededBrands = cars.stream()
                .filter(car -> car != null && car.getCarBrand() == null)
                .map(Car::getBrand)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return brandRepository.findAllByBrandIn(neededBrands).stream()
                .collect(Collectors.toMap(CarBrand::getBrand, Function.identity()));
    }

    private Car enrichCarWithBrand(Car car, Map<String, CarBrand> brandCache) {
        if (car == null || car.getCarBrand() != null) {
            return car;
        }

        return Optional.ofNullable(car.getBrand())
                .map(brandCache::get)
                .map(brand -> createCarWithBrand(car, brand))
                .orElse(car);
    }

    private Car createCarWithBrand(Car original, CarBrand brand) {
        return new Car.Builder()
                .type(original.getType())
                .brand(original.getBrand())
                .model(original.getModel())
                .prices(original.getPrices())
                .carBrand(brand)
                .build();
    }

    private Comparator<Car> createPriceComparator() {
        return Comparator.comparing(
                this::extractPriceSafe,
                PRICE_COMPARATOR
        );
    }

    private BigDecimal extractPriceSafe(Car car) {
        try {
            return Optional.ofNullable(car)
                    .map(Car::getPrices)
                    .map(prices -> prices.get(currencyCode))
                    .orElse(DEFAULT_PRICE);
        } catch (Exception e) {
            return DEFAULT_PRICE;
        }
    }
}
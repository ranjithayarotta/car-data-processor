package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReleaseDateSortStrategy implements SortStrategy {
    private static final Comparator<LocalDate> DATE_COMPARATOR =
            Comparator.nullsLast(Comparator.reverseOrder());

    private final BrandRepository brandRepository;

    public ReleaseDateSortStrategy(BrandRepository brandRepository) {
        this.brandRepository = Objects.requireNonNull(brandRepository,
                "BrandRepository cannot be null");
    }

    @Override
    public List<Car> sort(List<Car> cars) {
        Objects.requireNonNull(cars, "Car list cannot be null");

        if (cars.isEmpty()) {
            return Collections.emptyList();
        }

        // Bulk fetch all needed brands first
        Map<String, CarBrand> brandCache = fetchBrandsForCars(cars);

        // Process all cars in a single stream pipeline
        return cars.stream()
                .filter(Objects::nonNull)
                .map(car -> enrichCarWithBrand(car, brandCache))
                .sorted(createReleaseDateComparator())
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

    private Comparator<Car> createReleaseDateComparator() {
        return Comparator.comparing(
                this::extractReleaseDate,
                DATE_COMPARATOR
        );
    }

    private LocalDate extractReleaseDate(Car car) {
        return Optional.ofNullable(car)
                .map(Car::getCarBrand)
                .map(CarBrand::getReleaseDate)
                .orElse(null);
    }
}
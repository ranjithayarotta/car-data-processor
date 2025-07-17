package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeCurrencySortStrategy implements SortStrategy {
    private static final String DEFAULT_CURRENCY = "USD";

    private final Map<String, String> typeToCurrencyMap;
    private final boolean ascending;
    private final BrandRepository brandRepository;

    public TypeCurrencySortStrategy(BrandRepository brandRepository) {
        this(Map.of(
                "SUV", "EUR",
                "SEDAN", "JPY",
                "TRUCK", "USD"
        ), true, brandRepository);
    }

    public TypeCurrencySortStrategy(Map<String, String> typeCurrencyMap,
                                    boolean ascending,
                                    BrandRepository brandRepository) {
        this.typeToCurrencyMap = typeCurrencyMap.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toUpperCase(),
                        e -> e.getValue().toUpperCase()
                ));
        this.ascending = ascending;
        this.brandRepository = Objects.requireNonNull(brandRepository);
    }

    @Override
    public List<Car> sort(List<Car> cars) {
        if (cars == null || cars.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Prepare brand cache
        Map<String, CarBrand> brandCache = fetchMissingBrands(cars);

        // 2. Group and enrich cars by type
        Map<String, List<Car>> carsGroupedByType = cars.stream()
                .filter(Objects::nonNull)
                .map(car -> enrichWithBrand(car, brandCache))
                .collect(Collectors.groupingBy(
                        car -> Optional.ofNullable(car.getType()).orElse("").toUpperCase()
                ));
        List<Car> sortedCars = new ArrayList<>();
        for (String type : typeToCurrencyMap.keySet()) {
            List<Car> carsOfType = carsGroupedByType.remove(type);
            if (carsOfType != null) {
                carsOfType.sort(createPriceComparator(typeToCurrencyMap.get(type)));
                sortedCars.addAll(carsOfType);
            }
        }
        for (List<Car> remainingCars : carsGroupedByType.values()) {
            remainingCars.sort(createPriceComparator(DEFAULT_CURRENCY));
            sortedCars.addAll(remainingCars);
        }

        return ascending ? sortedCars : reverse(sortedCars);
    }

    private Comparator<Car> createPriceComparator(String currency) {
        Comparator<BigDecimal> priceComparator = Comparator.nullsLast(BigDecimal::compareTo);
        if (!ascending) {
            priceComparator = priceComparator.reversed();
        }

        return Comparator.comparing(
                car -> Optional.ofNullable(car.getPrices()).map(p -> p.get(currency)).orElse(null),
                priceComparator
        );
    }

    private List<Car> reverse(List<Car> cars) {
        List<Car> reversed = new ArrayList<>(cars);
        Collections.reverse(reversed);
        return reversed;
    }

    private Map<String, CarBrand> fetchMissingBrands(List<Car> cars) {
        Set<String> brandsToFetch = cars.stream()
                .filter(car -> car != null && car.getCarBrand() == null && car.getBrand() != null)
                .map(Car::getBrand)
                .collect(Collectors.toSet());

        return brandRepository.findAllByBrandIn(brandsToFetch).stream()
                .collect(Collectors.toMap(CarBrand::getBrand, Function.identity()));
    }

    private Car enrichWithBrand(Car car, Map<String, CarBrand> brandCache) {
        if (car == null || car.getCarBrand() != null || car.getBrand() == null) {
            return car;
        }

        CarBrand fetchedBrand = brandCache.get(car.getBrand());
        if (fetchedBrand == null) {
            return car;
        }

        return new Car.Builder()
                .type(car.getType())
                .brand(car.getBrand())
                .model(car.getModel())
                .prices(car.getPrices())
                .carBrand(fetchedBrand)
                .build();
    }
}

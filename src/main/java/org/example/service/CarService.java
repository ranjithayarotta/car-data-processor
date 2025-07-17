package org.example.service;

import org.example.factory.CarFilterFactory;
import org.example.factory.CarSortFactory;
import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.strategy.FilterStrategy;
import org.example.strategy.SortStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class CarService {
    private final CarFilterFactory filterFactory;
    private final CarRepository carRepository;
    private final CarSortFactory sortFactory;
    public CarService(CarFilterFactory filterFactory,
                      CarRepository carRepository,
                      CarSortFactory sortFactory) {
        this.filterFactory = filterFactory;
        this.carRepository = carRepository;
        this.sortFactory = sortFactory;
    }
    private List<Car> getAllCars() {
        return carRepository.findAll();
    }
    public List<Car> filterCarsByBrandAndPrice(String brand,
                                               BigDecimal minPrice,
                                               BigDecimal maxPrice) {
        FilterStrategy filter = filterFactory.createBrandPriceFilter(brand, minPrice, maxPrice);
        return applyFilter(filter);
    }
    public List<Car> filterByBrandAndReleaseDate(String brand,
                                                 LocalDate startDate,
                                                 LocalDate endDate) {
        FilterStrategy filter = filterFactory.createBrandDateFilter(brand, startDate, endDate);
        return applyFilter(filter);
    }
    public List<Car> sortCarsByPrice() {
        return applySort(sortFactory.createPriceSorter());
    }
    public List<Car> sortCarsByReleaseDate() {
        return applySort(sortFactory.createReleaseDateSorter());
    }
    public List<Car> sortCarsByTypeAndCurrency() {
        return applySort(sortFactory.createTypeCurrencySorter());
    }

    // Private helper methods
    private List<Car> applyFilter(FilterStrategy filter) {
        return getAllCars().stream()
                .filter(Objects::nonNull)
                .filter(filter::filter)
                .toList();
    }
    private List<Car> applySort(SortStrategy sorter) {
        return applySort(sorter, getAllCars());
    }
    private List<Car> applySort(SortStrategy sorter, List<Car> cars) {
        return sorter.sort(cars);
    }
}
package org.example.service;

import org.example.factory.CarFilterFactory;
import org.example.factory.CarSortFactory;
import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.strategy.FilterStrategy;
import org.example.strategy.SortStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CarServiceTest {

    private CarFilterFactory filterFactory;
    private CarSortFactory sortFactory;
    private CarRepository carRepository;
    private CarService carService;

    private List<Car> mockCars;

    @BeforeEach
    public void setUp() {
        filterFactory = mock(CarFilterFactory.class);
        sortFactory = mock(CarSortFactory.class);
        carRepository = mock(CarRepository.class);
        carService = new CarService(filterFactory, carRepository, sortFactory);

        mockCars = List.of(
                new Car.Builder()
                        .brand("Toyota")
                        .model("Camry")
                        .type("SEDAN")
                        .prices(Map.of("USD", new BigDecimal("25000")))
                        .carBrand(new CarBrand.Builder()
                                .brand("Toyota")
                                .releaseDate(LocalDate.of(2021, 5, 10))
                                .build())
                        .build(),
                new Car.Builder()
                        .brand("Ford")
                        .model("F-150")
                        .type("TRUCK")
                        .prices(Map.of("USD", new BigDecimal("40000")))
                        .carBrand(new CarBrand.Builder()
                                .brand("Ford")
                                .releaseDate(LocalDate.of(2022, 1, 15))
                                .build())
                        .build()
        );

        when(carRepository.findAll()).thenReturn(mockCars);
    }

    @Test
    void testFilterCarsByBrandAndPrice() {
        FilterStrategy strategy = mock(FilterStrategy.class);
        when(filterFactory.createBrandPriceFilter("Toyota", BigDecimal.ZERO, BigDecimal.valueOf(30000)))
                .thenReturn(strategy);

        when(strategy.filter(any())).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            return "Toyota".equals(car.getBrand());
        });

        List<Car> result = carService.filterCarsByBrandAndPrice("Toyota", BigDecimal.ZERO, BigDecimal.valueOf(30000));

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void testFilterByBrandAndReleaseDate() {
        FilterStrategy strategy = mock(FilterStrategy.class);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2021, 12, 31);

        when(filterFactory.createBrandDateFilter("Toyota", start, end)).thenReturn(strategy);

        when(strategy.filter(any())).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            return "Toyota".equals(car.getBrand()) &&
                    car.getCarBrand().getReleaseDate().compareTo(start) >= 0 &&
                    car.getCarBrand().getReleaseDate().compareTo(end) <= 0;
        });

        List<Car> result = carService.filterByBrandAndReleaseDate("Toyota", start, end);

        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void testSortCarsByPrice() {
        SortStrategy strategy = mock(SortStrategy.class);
        when(sortFactory.createPriceSorter()).thenReturn(strategy);

        List<Car> reversed = List.of(mockCars.get(1), mockCars.get(0));
        when(strategy.sort(mockCars)).thenReturn(reversed);

        List<Car> result = carService.sortCarsByPrice();

        assertEquals(2, result.size());
        assertEquals("Ford", result.get(0).getBrand());
    }

    @Test
    void testSortCarsByReleaseDate() {
        SortStrategy strategy = mock(SortStrategy.class);
        when(sortFactory.createReleaseDateSorter()).thenReturn(strategy);
        when(strategy.sort(mockCars)).thenReturn(mockCars);

        List<Car> result = carService.sortCarsByReleaseDate();

        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }

    @Test
    void testSortCarsByTypeAndCurrency() {
        SortStrategy strategy = mock(SortStrategy.class);
        when(sortFactory.createTypeCurrencySorter()).thenReturn(strategy);
        when(strategy.sort(mockCars)).thenReturn(mockCars);

        List<Car> result = carService.sortCarsByTypeAndCurrency();

        assertEquals(2, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }


    @Test
    void testFilterCarsByBrandAndPrice_NoMatch() {
        FilterStrategy strategy = mock(FilterStrategy.class);

        when(filterFactory.createBrandPriceFilter("BMW", BigDecimal.ZERO, BigDecimal.valueOf(20000)))
                .thenReturn(strategy);

        when(strategy.filter(any())).thenReturn(false); // No car matches

        List<Car> result = carService.filterCarsByBrandAndPrice("BMW", BigDecimal.ZERO, BigDecimal.valueOf(20000));
        assertEquals(0, result.size());
    }

    @Test
    void testFilterByBrandAndReleaseDate_NoMatch() {
        FilterStrategy strategy = mock(FilterStrategy.class);
        LocalDate start = LocalDate.of(2010, 1, 1);
        LocalDate end = LocalDate.of(2011, 1, 1);

        when(filterFactory.createBrandDateFilter("Toyota", start, end)).thenReturn(strategy);
        when(strategy.filter(any())).thenReturn(false);

        List<Car> result = carService.filterByBrandAndReleaseDate("Toyota", start, end);
        assertEquals(0, result.size());
    }

    @Test
    void testSortCarsByPrice_EmptyList() {
        SortStrategy strategy = mock(SortStrategy.class);
        when(sortFactory.createPriceSorter()).thenReturn(strategy);
        when(strategy.sort(mockCars)).thenReturn(List.of());

        List<Car> result = carService.sortCarsByPrice();
        assertEquals(0, result.size());
    }

    @Test
    void testRepositoryReturnsEmptyList() {
        when(carRepository.findAll()).thenReturn(List.of());

        SortStrategy strategy = mock(SortStrategy.class);
        when(sortFactory.createReleaseDateSorter()).thenReturn(strategy);
        when(strategy.sort(List.of())).thenReturn(List.of());

        List<Car> result = carService.sortCarsByReleaseDate();
        assertEquals(0, result.size());
    }

    @Test
    void testCarWithNullCarBrandDoesNotCrash() {
        Car carWithoutBrand = new Car.Builder()
                .brand("Unknown")
                .model("X")
                .type("SUV")
                .prices(Map.of("USD", BigDecimal.TEN))
                .carBrand(null)
                .build();

        when(carRepository.findAll()).thenReturn(List.of(carWithoutBrand));

        FilterStrategy strategy = mock(FilterStrategy.class);
        when(filterFactory.createBrandDateFilter(any(), any(), any())).thenReturn(strategy);

        // Should not crash even though carBrand is null
        when(strategy.filter(any())).thenReturn(true);

        List<Car> result = carService.filterByBrandAndReleaseDate("Unknown", LocalDate.MIN, LocalDate.MAX);
        assertEquals(1, result.size());
    }

}

package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PriceSortStrategyTest {

    private BrandRepository brandRepository;
    private PriceSortStrategy strategy;

    private final Car carA = new Car.Builder()
            .brand("Toyota")
            .model("Corolla")
            .type("SEDAN")
            .prices(Map.of("USD", new BigDecimal("15000")))
            .build();

    private final Car carB = new Car.Builder()
            .brand("Ford")
            .model("F-150")
            .type("TRUCK")
            .prices(Map.of("USD", new BigDecimal("40000")))
            .build();

    private final Car carC = new Car.Builder()
            .brand("Honda")
            .model("Civic")
            .type("SEDAN")
            .prices(Map.of("USD", new BigDecimal("25000")))
            .build();

    private final Car carWithoutPrice = new Car.Builder()
            .brand("Chevy")
            .model("Impala")
            .type("SEDAN")
            .prices(Collections.emptyMap())
            .build();

    @BeforeEach
    void setUp() {
        brandRepository = mock(BrandRepository.class);
        strategy = new PriceSortStrategy(brandRepository);

        when(brandRepository.findAllByBrandIn(Set.of("Toyota", "Ford", "Honda", "Chevy")))
                .thenReturn(List.of(
                        new CarBrand.Builder().brand("Toyota").releaseDate(LocalDate.of(2020, 1, 1)).build(),
                        new CarBrand.Builder().brand("Ford").releaseDate(LocalDate.of(2019, 5, 5)).build(),
                        new CarBrand.Builder().brand("Honda").releaseDate(LocalDate.of(2021, 7, 7)).build(),
                        new CarBrand.Builder().brand("Chevy").releaseDate(LocalDate.of(2022, 3, 3)).build()
                ));
    }

    @Test
    void testSort_ByPriceDescending() {
        List<Car> unsorted = List.of(carA, carB, carC);
        List<Car> result = strategy.sort(unsorted);

        assertEquals(3, result.size());
        assertEquals("Ford", result.get(0).getBrand());
        assertEquals("Honda", result.get(1).getBrand());
        assertEquals("Toyota", result.get(2).getBrand());
    }

    @Test
    void testSort_IncludeCarsWithoutPrice() {
        List<Car> cars = List.of(carA, carWithoutPrice, carB);
        List<Car> result = strategy.sort(cars);

        assertEquals(3, result.size());
        assertEquals("Ford", result.get(0).getBrand());
        assertEquals("Toyota", result.get(1).getBrand());
        assertEquals("Chevy", result.get(2).getBrand());
    }

    @Test
    void testSort_NullList_ReturnsEmpty() {
        List<Car> result = strategy.sort(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSort_EmptyList_ReturnsEmpty() {
        List<Car> result = strategy.sort(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void testSort_NullCarInList_IsIgnoredGracefully() {
        List<Car> result = strategy.sort(new ArrayList<>(Arrays.asList(carA, null, carB)));
        assertEquals(2, result.size());
        assertEquals("Ford", result.get(0).getBrand());
        assertEquals("Toyota", result.get(1).getBrand());
    }

    @Test
    void testSort_CarWithoutBrandInRepo_HandledGracefully() {
        when(brandRepository.findAllByBrandIn(any()))
                .thenReturn(List.of());

        List<Car> result = strategy.sort(List.of(carA, carB));
        assertEquals(2, result.size());
    }
}

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

class TypeCurrencySortStrategyTest {

    private BrandRepository brandRepository;
    private TypeCurrencySortStrategy strategy;

    private Car suvCar;
    private Car sedanCar;
    private Car truckCar;
    private Car unknownCar;

    @BeforeEach
    void setUp() {
        brandRepository = mock(BrandRepository.class);
        strategy = new TypeCurrencySortStrategy(brandRepository);

        suvCar = new Car.Builder()
                .type("SUV")
                .brand("BMW")
                .model("X5")
                .prices(Map.of("EUR", BigDecimal.valueOf(50000)))
                .build();

        sedanCar = new Car.Builder()
                .type("SEDAN")
                .brand("Toyota")
                .model("Camry")
                .prices(Map.of("JPY", BigDecimal.valueOf(3000000)))
                .build();

        truckCar = new Car.Builder()
                .type("TRUCK")
                .brand("Ford")
                .model("F-150")
                .prices(Map.of("USD", BigDecimal.valueOf(40000)))
                .build();

        unknownCar = new Car.Builder()
                .type("VAN")
                .brand("Hyundai")
                .model("H1")
                .prices(Map.of("USD", BigDecimal.valueOf(20000)))
                .build();

        when(brandRepository.findAllByBrandIn(anySet()))
                .thenReturn(List.of(
                        new CarBrand.Builder().brand("BMW").releaseDate(LocalDate.of(2020, 1, 1)).build(),
                        new CarBrand.Builder().brand("Toyota").releaseDate(LocalDate.of(2019, 1, 1)).build(),
                        new CarBrand.Builder().brand("Ford").releaseDate(LocalDate.of(2018, 1, 1)).build(),
                        new CarBrand.Builder().brand("Hyundai").releaseDate(LocalDate.of(2021, 1, 1)).build()
                ));
    }

    @Test
    void testSort_ByTypeAndCurrency_Ascending() {
        List<Car> cars = List.of(suvCar, sedanCar, truckCar, unknownCar);
        List<Car> sorted = strategy.sort(cars);

        assertEquals(4, sorted.size());
        assertEquals("BMW", sorted.get(0).getBrand());
        assertEquals("Ford", sorted.get(1).getBrand());
        assertEquals("Toyota", sorted.get(2).getBrand());
        assertEquals("Hyundai", sorted.get(3).getBrand());
    }

    @Test
    void testSort_ByTypeAndCurrency_Descending() {
        TypeCurrencySortStrategy descStrategy = new TypeCurrencySortStrategy(
                Map.of("SUV", "EUR", "SEDAN", "JPY", "TRUCK", "USD"),
                false,
                brandRepository
        );

        List<Car> cars = List.of(suvCar, sedanCar, truckCar, unknownCar);
        List<Car> sorted = descStrategy.sort(cars);

        assertEquals(4, sorted.size());
        assertEquals("Hyundai", sorted.get(0).getBrand());
        assertEquals("Toyota", sorted.get(1).getBrand());
        assertEquals("Ford", sorted.get(2).getBrand());
        assertEquals("BMW", sorted.get(3).getBrand());
    }

    @Test
    void testSort_EmptyListReturnsEmpty() {
        List<Car> result = strategy.sort(List.of());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSort_NullInputReturnsEmpty() {
        List<Car> result = strategy.sort(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSort_UnknownTypeFallsBackToDefaultCurrency() {
        Car other = new Car.Builder()
                .type("BIKE")
                .brand("Yamaha")
                .model("R15")
                .prices(Map.of("USD", BigDecimal.valueOf(5000)))
                .build();

        when(brandRepository.findAllByBrandIn(anySet()))
                .thenReturn(List.of(new CarBrand.Builder().brand("Yamaha").releaseDate(LocalDate.of(2022, 5, 5)).build()));

        List<Car> sorted = strategy.sort(List.of(other));
        assertEquals(1, sorted.size());
        assertEquals("Yamaha", sorted.get(0).getBrand());
    }

    @Test
    void testSort_CarsWithoutPricesHandledGracefully() {
        Car noPriceCar = new Car.Builder()
                .type("SUV")
                .brand("Mazda")
                .model("CX-5")
                .prices(Collections.emptyMap())
                .build();

        when(brandRepository.findAllByBrandIn(anySet()))
                .thenReturn(List.of(new CarBrand.Builder().brand("Mazda").releaseDate(LocalDate.of(2023, 3, 3)).build()));

        List<Car> sorted = strategy.sort(List.of(noPriceCar));
        assertEquals(1, sorted.size());
        assertEquals("Mazda", sorted.get(0).getBrand());
    }
}

package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReleaseDateSortStrategyTest {

    private BrandRepository brandRepository;
    private ReleaseDateSortStrategy strategy;

    private Car car1;
    private Car car2;
    private Car car3;
    private Car carNoBrand;

    @BeforeEach
    void setUp() {
        brandRepository = mock(BrandRepository.class);
        strategy = new ReleaseDateSortStrategy(brandRepository);
        car1 = new Car.Builder()
                .type("SEDAN")
                .brand("Toyota")
                .model("Camry")
                .prices(Map.of("USD", java.math.BigDecimal.valueOf(25000)))
                .build();
        car2 = new Car.Builder()
                .type("SUV")
                .brand("Ford")
                .model("Explorer")
                .prices(Map.of("USD", java.math.BigDecimal.valueOf(32000)))
                .build();
        car3 = new Car.Builder()
                .type("TRUCK")
                .brand("Honda")
                .model("Ridgeline")
                .prices(Map.of("USD", java.math.BigDecimal.valueOf(28000)))
                .build();
        carNoBrand = new Car.Builder()
                .type("COUPE")
                .brand("Unknown")
                .model("Mystery")
                .prices(Map.of("USD", java.math.BigDecimal.valueOf(10000)))
                .build();
        when(brandRepository.findAllByBrandIn(Set.of("Toyota", "Ford", "Honda", "Unknown")))
                .thenReturn(List.of(
                        new CarBrand.Builder().brand("Toyota").releaseDate(LocalDate.of(2022, 5, 1)).build(),
                        new CarBrand.Builder().brand("Ford").releaseDate(LocalDate.of(2021, 3, 10)).build(),
                        new CarBrand.Builder().brand("Honda").releaseDate(LocalDate.of(2023, 7, 20)).build()
                ));
    }

    @Test
    void testSort_ByReleaseDateDescending() {
        List<Car> cars = List.of(car1, car2, car3);
        List<Car> sorted = strategy.sort(cars);
        assertEquals(3, sorted.size());
        assertEquals("Toyota", sorted.get(0).getBrand());
        assertEquals("Ford", sorted.get(1).getBrand());
        assertEquals("Honda", sorted.get(2).getBrand());
    }

    @Test
    void testSort_MissingBrandDataHandledGracefully() {
        List<Car> cars = List.of(car1, carNoBrand, car2);
        List<Car> sorted = strategy.sort(cars);
        assertEquals(3, sorted.size());
        assertEquals("Toyota", sorted.get(0).getBrand());
        assertEquals("Unknown", sorted.get(1).getBrand());
        assertEquals("Ford", sorted.get(2).getBrand());
    }

    @Test
    void testSort_EmptyListReturnsEmpty() {
        List<Car> sorted = strategy.sort(List.of());
        assertNotNull(sorted);
        assertTrue(sorted.isEmpty());
    }

    @Test
    void testSort_NullListThrowsException() {
        Exception ex = assertThrows(NullPointerException.class, () -> strategy.sort(null));
        assertEquals("Car list cannot be null", ex.getMessage());
    }

    @Test
    void testSort_AlreadyEnrichedCars_NotQueriedAgain() {
        Car enrichedCar = new Car.Builder()
                .type("HATCHBACK")
                .brand("Nissan")
                .model("Leaf")
                .prices(Collections.emptyMap())
                .carBrand(new CarBrand.Builder().brand("Nissan").releaseDate(LocalDate.of(2020, 1, 1)).build())
                .build();
        List<Car> result = strategy.sort(List.of(enrichedCar));
        assertEquals(1, result.size());
        verify(brandRepository).findAllByBrandIn(Collections.emptySet());
    }

}

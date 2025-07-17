package org.example.strategy;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.BrandRepository;
import org.example.strategy.BrandDateFilterStrategy;
import org.example.strategy.FilterStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrandDateFilterStrategyTest {

    private BrandRepository brandRepository;

    private final LocalDate START = LocalDate.of(2020, 1, 1);
    private final LocalDate END = LocalDate.of(2023, 1, 1);

    @BeforeEach
    void setup() {
        brandRepository = mock(BrandRepository.class);
    }

    @Test
    void testFilter_Pass_MatchingBrandAndDateInRange() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Collections.emptyMap())
                .build();

        CarBrand carBrand = new CarBrand.Builder()
                .brand("Toyota")
                .releaseDate(LocalDate.of(2021, 6, 15))
                .build();

        when(brandRepository.findByBrand("Toyota")).thenReturn(Optional.of(carBrand));

        FilterStrategy strategy = new BrandDateFilterStrategy("Toyota", START, END, brandRepository);
        assertTrue(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_BrandDoesNotMatch() {
        Car car = new Car.Builder()
                .brand("Honda")
                .prices(Collections.emptyMap())
                .build();

        FilterStrategy strategy = new BrandDateFilterStrategy("Toyota", START, END, brandRepository);
        assertFalse(strategy.filter(car));
        verify(brandRepository, never()).findByBrand(any());
    }

    @Test
    void testFilter_Fail_ReleaseDateOutOfRange() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Collections.emptyMap())
                .build();

        CarBrand carBrand = new CarBrand.Builder()
                .brand("Toyota")
                .releaseDate(LocalDate.of(2010, 1, 1))
                .build();

        when(brandRepository.findByBrand("Toyota")).thenReturn(Optional.of(carBrand));

        FilterStrategy strategy = new BrandDateFilterStrategy("Toyota", START, END, brandRepository);
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_CarIsNull() {
        FilterStrategy strategy = new BrandDateFilterStrategy("Toyota", START, END, brandRepository);
        assertFalse(strategy.filter(null));
    }

    @Test
    void testFilter_Fail_BrandRepoReturnsEmpty() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Collections.emptyMap())
                .build();

        when(brandRepository.findByBrand("Toyota")).thenReturn(Optional.empty());

        FilterStrategy strategy = new BrandDateFilterStrategy("Toyota", START, END, brandRepository);
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_RepoThrowsException() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Collections.emptyMap())
                .build();

        when(brandRepository.findByBrand("Toyota")).thenThrow(new RuntimeException("DB error"));

        FilterStrategy strategy = new BrandDateFilterStrategy("Toyota", START, END, brandRepository);
        assertFalse(strategy.filter(car));
    }

    @Test
    void testConstructor_InvalidDateRange_ThrowsException() {
        LocalDate badStart = LocalDate.of(2025, 1, 1);
        LocalDate badEnd = LocalDate.of(2020, 1, 1);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new BrandDateFilterStrategy("Toyota", badStart, badEnd, brandRepository)
        );

        assertEquals("Start date cannot be after end date", exception.getMessage());
    }
}

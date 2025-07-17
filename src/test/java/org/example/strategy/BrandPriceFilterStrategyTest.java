package org.example.strategy;

import org.example.model.Car;
import org.example.strategy.BrandPriceFilterStrategy;
import org.example.strategy.FilterStrategy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BrandPriceFilterStrategyTest {

    @Test
    void testFilter_Pass_BrandAndPriceInRange() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Map.of("USD", new BigDecimal("25000")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertTrue(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_BrandMismatch() {
        Car car = new Car.Builder()
                .brand("Honda")
                .prices(Map.of("USD", new BigDecimal("25000")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_PriceTooLow() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Map.of("USD", new BigDecimal("15000")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_PriceTooHigh() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Map.of("USD", new BigDecimal("35000")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_NoPriceForCurrency() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Map.of("EUR", new BigDecimal("25000")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_PriceMapIsNull() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Collections.emptyMap())
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertFalse(strategy.filter(car));
    }

    @Test
    void testFilter_Fail_CarIsNull() {
        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertFalse(strategy.filter(null));
    }

    @Test
    void testFilter_UsesDefaultMinAndMaxWhenNull() {
        Car car = new Car.Builder()
                .brand("Toyota")
                .prices(Map.of("USD", new BigDecimal("999999999")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("Toyota", null, null);
        assertTrue(strategy.filter(car));
    }

    @Test
    void testConstructor_Fail_MinGreaterThanMax() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new BrandPriceFilterStrategy("Toyota", new BigDecimal("50000"), new BigDecimal("10000")));

        assertEquals("Min price cannot be greater than max price", ex.getMessage());
    }

    @Test
    void testFilter_CaseInsensitiveBrandMatch() {
        Car car = new Car.Builder()
                .brand("TOYOTA")
                .prices(Map.of("USD", new BigDecimal("25000")))
                .build();

        FilterStrategy strategy = new BrandPriceFilterStrategy("toyota", new BigDecimal("20000"), new BigDecimal("30000"));
        assertTrue(strategy.filter(car));
    }
}

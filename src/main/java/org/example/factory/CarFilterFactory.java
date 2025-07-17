package org.example.factory;

import org.example.repository.BrandRepository;
import org.example.strategy.BrandDateFilterStrategy;
import org.example.strategy.BrandPriceFilterStrategy;
import org.example.strategy.FilterStrategy;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CarFilterFactory {
    private final BrandRepository brandRepository;

    public CarFilterFactory(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public FilterStrategy createBrandPriceFilter(String brand, BigDecimal minPrice, BigDecimal maxPrice) {
        return new BrandPriceFilterStrategy(brand, minPrice, maxPrice);
    }

    public FilterStrategy createBrandDateFilter(String brand, LocalDate startDate, LocalDate endDate) {
        return new BrandDateFilterStrategy(brand, startDate, endDate, brandRepository);
    }
}
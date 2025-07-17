package org.example.factory;

import org.example.repository.BrandRepository;
import org.example.strategy.PriceSortStrategy;
import org.example.strategy.ReleaseDateSortStrategy;
import org.example.strategy.SortStrategy;
import org.example.strategy.TypeCurrencySortStrategy;

public class CarSortFactory {
    private final BrandRepository brandRepository;

    public CarSortFactory(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public SortStrategy createPriceSorter() {
        return new PriceSortStrategy(brandRepository);
    }

    public SortStrategy createReleaseDateSorter() {
        return new ReleaseDateSortStrategy(brandRepository);
    }

    public SortStrategy createTypeCurrencySorter() {
        return new TypeCurrencySortStrategy(brandRepository);
    }
}
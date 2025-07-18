package org.example.repository;

import org.example.model.CarBrand;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBrandRepository implements BrandRepository {
    private final List<CarBrand> brands;

    public InMemoryBrandRepository(List<CarBrand> brands) {
        this.brands = new ArrayList<>(brands);
    }

    @Override
    public Optional<CarBrand> findByBrand(String brand) {
        return brands.stream()
                .filter(b -> b.getBrand().equalsIgnoreCase(brand))
                .findFirst();
    }

    @Override
    public List<CarBrand> findAllByBrandIn(Collection<String> brandNames) {
        if (brandNames == null || brandNames.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> lowercaseBrandNames = brandNames.stream()
                .filter(Objects::nonNull)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return brands.stream()
                .filter(brand -> lowercaseBrandNames.contains(brand.getBrand().toLowerCase()))
                .collect(Collectors.toList());
    }

}
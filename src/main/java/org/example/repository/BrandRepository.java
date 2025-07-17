package org.example.repository;

import org.example.model.Car;
import org.example.model.CarBrand;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BrandRepository {
    Optional<CarBrand> findByBrand(String brand);
    List<CarBrand> findAllByBrandIn(Collection<String> brandNames);
}
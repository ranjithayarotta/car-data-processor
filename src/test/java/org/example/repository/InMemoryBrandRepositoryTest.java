package org.example.repository;

import org.example.model.CarBrand;
import org.example.repository.InMemoryBrandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBrandRepositoryTest {

    private InMemoryBrandRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBrandRepository(List.of(
                new CarBrand.Builder().brand("Toyota").releaseDate(LocalDate.of(2020, 1, 1)).build(),
                new CarBrand.Builder().brand("Ford").releaseDate(LocalDate.of(2019, 5, 10)).build(),
                new CarBrand.Builder().brand("BMW").releaseDate(LocalDate.of(2021, 3, 15)).build()
        ));
    }

    @Test
    void testFindByBrand_Exists() {
        Optional<CarBrand> result = repository.findByBrand("Toyota");
        assertTrue(result.isPresent());
        assertEquals("Toyota", result.get().getBrand());
    }

    @Test
    void testFindByBrand_CaseInsensitive() {
        Optional<CarBrand> result = repository.findByBrand("bmw");
        assertTrue(result.isPresent());
        assertEquals("BMW", result.get().getBrand());
    }

    @Test
    void testFindByBrand_NotExists() {
        Optional<CarBrand> result = repository.findByBrand("Honda");
        assertFalse(result.isPresent());
    }

    @Test
    void testFindAllByBrandIn_MultipleMatch() {
        List<CarBrand> result = repository.findAllByBrandIn(List.of("TOYOTA", "FORD"));
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(b -> b.getBrand().equals("Toyota")));
        assertTrue(result.stream().anyMatch(b -> b.getBrand().equals("Ford")));
    }

    @Test
    void testFindAllByBrandIn_CaseInsensitive() {
        List<CarBrand> result = repository.findAllByBrandIn(List.of("bmw"));
        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).getBrand());
    }

    @Test
    void testFindAllByBrandIn_EmptyInput() {
        List<CarBrand> result = repository.findAllByBrandIn(List.of());
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByBrandIn_NullInput() {
        List<CarBrand> result = repository.findAllByBrandIn(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindAllByBrandIn_WithNullBrandInInput() {
        List<CarBrand> result = repository.findAllByBrandIn(Arrays.asList("Toyota", null));
        assertEquals(1, result.size());
        assertEquals("Toyota", result.get(0).getBrand());
    }
}

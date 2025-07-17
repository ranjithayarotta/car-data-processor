package org.example.repository;

import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.repository.InMemoryCarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCarRepositoryTest {

    private InMemoryCarRepository repository;
    private Car toyota;
    private Car ford;

    @BeforeEach
    void setUp() {
        toyota = new Car.Builder()
                .type("SEDAN")
                .brand("Toyota")
                .model("Camry")
                .prices(Map.of("USD", BigDecimal.valueOf(25000)))
                .carBrand(new CarBrand.Builder().brand("Toyota").releaseDate(LocalDate.of(2021, 5, 10)).build())
                .build();

        ford = new Car.Builder()
                .type("TRUCK")
                .brand("Ford")
                .model("F-150")
                .prices(Map.of("USD", BigDecimal.valueOf(40000)))
                .carBrand(new CarBrand.Builder().brand("Ford").releaseDate(LocalDate.of(2022, 3, 1)).build())
                .build();

        repository = new InMemoryCarRepository(List.of(toyota, ford));
    }

    @Test
    void testFindAll_ReturnsAllCars() {
        List<Car> cars = repository.findAll();
        assertEquals(2, cars.size());
        assertTrue(cars.contains(toyota));
        assertTrue(cars.contains(ford));
    }

}

package org.example.repository;

import org.example.model.Car;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryCarRepository implements CarRepository {
    private final List<Car> cars;
    public InMemoryCarRepository(List<Car> cars) {
        this.cars = new ArrayList<>(cars);
    }
    @Override
    public List<Car> findAll() {
        return new ArrayList<>(cars);
    }
}
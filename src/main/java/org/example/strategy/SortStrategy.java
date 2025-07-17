package org.example.strategy;

import org.example.model.Car;

import java.util.List;

@FunctionalInterface
public interface SortStrategy {
    List<Car> sort(List<Car> cars);
}
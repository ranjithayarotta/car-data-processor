package org.example.strategy;

import org.example.model.Car;

public interface FilterStrategy {
    boolean filter(Car car);
}
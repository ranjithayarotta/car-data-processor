package org.example.output;

import org.example.model.Car;

import java.util.List;

public interface OutputFormatter {
    String format(List<Car> cars);
}
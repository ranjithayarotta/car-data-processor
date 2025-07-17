package org.example.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class Car {
    private final String type;
    private final String brand;
    private final String model;
    private final Map<String, BigDecimal> prices;
    private final CarBrand carBrand;  // New field

    private Car(Builder builder) {
        this.type = builder.type;
        this.brand = builder.brand;
        this.model = builder.model;
        this.prices = Collections.unmodifiableMap(builder.prices);
        this.carBrand = builder.carBrand;  // Initialize from builder
    }

    // Getters
    public String getType() { return type; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Map<String, BigDecimal> getPrices() { return prices; }
    public CarBrand getCarBrand() { return carBrand; }  // New getter

    // Builder
    public static class Builder {
        private String type;
        private String brand;
        private String model;
        private Map<String, BigDecimal> prices;
        private CarBrand carBrand;  // New builder field

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder prices(Map<String, BigDecimal> prices) {
            this.prices = prices;
            return this;
        }

        public Builder carBrand(CarBrand carBrand) {  // New builder method
            this.carBrand = carBrand;
            return this;
        }

        public Car build() {
            return new Car(this);
        }
    }

    // Optionally add equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(type, car.type) &&
                Objects.equals(brand, car.brand) &&
                Objects.equals(model, car.model) &&
                Objects.equals(prices, car.prices) &&
                Objects.equals(carBrand, car.carBrand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, brand, model, prices, carBrand);
    }

    @Override
    public String toString() {
        return "Car{" +
                "type='" + type + '\'' +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", prices=" + prices +
                ", carBrand=" + carBrand +
                '}';
    }
}
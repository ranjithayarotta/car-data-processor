package org.example.model;

import java.time.LocalDate;
import java.util.Objects;

public final class CarBrand {
    private final String brand;
    private final LocalDate releaseDate;

    private CarBrand(Builder builder) {
        this.brand = Objects.requireNonNull(builder.brand, "Brand cannot be null");
        this.releaseDate = Objects.requireNonNull(builder.releaseDate, "Release date cannot be null");
    }
    public String getBrand() {
        return brand;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }
    public static class Builder {
        private String brand;
        private LocalDate releaseDate;

        public Builder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public CarBrand build() {
            return new CarBrand(this);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarBrand carBrand = (CarBrand) o;
        return brand.equals(carBrand.brand) && 
               releaseDate.equals(carBrand.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, releaseDate);
    }
    @Override
    public String toString() {
        return "CarBrand{" +
                "brand='" + brand + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
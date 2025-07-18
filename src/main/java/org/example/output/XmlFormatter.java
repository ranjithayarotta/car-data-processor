package org.example.output;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.example.exception.OutputFormatException;
import org.example.model.Car;
import org.example.model.CarBrand;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class XmlFormatter implements OutputFormatter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final JAXBContext jaxbContext;

    public XmlFormatter() {
        try {
            this.jaxbContext = JAXBContext.newInstance(CarListWrapper.class);
        } catch (JAXBException e) {
            throw new OutputFormatException("Failed to initialize XML formatter", e);
        }
    }

    @Override
    public String format(List<Car> cars) {
        try {
            List<SimpleCar> simpleCars = convertToSimpleCars(cars);

            CarListWrapper wrapper = new CarListWrapper(simpleCars);

            StringWriter writer = new StringWriter();
            Marshaller marshaller = jaxbContext.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            marshaller.marshal(wrapper, writer);
            return writer.toString();

        } catch (JAXBException e) {
            throw new OutputFormatException("Failed to generate XML output", e);
        }
    }

    private List<SimpleCar> convertToSimpleCars(List<Car> cars) {
        if (cars == null || cars.isEmpty()) return Collections.emptyList();

        return cars.stream()
                .filter(Objects::nonNull)
                .map(this::mapToSimpleCar)
                .toList();
    }

    private SimpleCar mapToSimpleCar(Car car) {
        String releaseDate = Optional.ofNullable(car.getCarBrand())
                .map(CarBrand::getReleaseDate)
                .map(DATE_FORMATTER::format)
                .orElse("N/A");

        return new SimpleCar(
                safeString(car.getType()),
                safeString(car.getBrand()),
                safeString(car.getModel()),
                extractUsdPrice(car),
                releaseDate
        );
    }

    private String safeString(String value) {
        return value != null ? value : "N/A";
    }

    private BigDecimal extractUsdPrice(Car car) {
        if (car == null || car.getPrices() == null) return BigDecimal.ZERO;
        return car.getPrices().getOrDefault("USD", BigDecimal.ZERO);
    }

    @XmlRootElement(name = "cars")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class CarListWrapper {

        @XmlElement(name = "car")
        private List<SimpleCar> cars;

        public CarListWrapper() {}

        public CarListWrapper(List<SimpleCar> cars) {
            this.cars = cars;
        }

        public List<SimpleCar> getCars() {
            return cars;
        }

        public void setCars(List<SimpleCar> cars) {
            this.cars = cars;
        }
    }

    @XmlRootElement(name = "car")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SimpleCar {

        @XmlElement
        private String type;

        @XmlElement
        private String brand;

        @XmlElement
        private String model;

        @XmlElement(name = "price")
        @XmlJavaTypeAdapter(PriceAdapter.class)
        private BigDecimal usdPrice;

        @XmlElement(name = "releaseDate")
        private String releaseDate;

        public SimpleCar() {}

        public SimpleCar(String type, String brand, String model, BigDecimal usdPrice, String releaseDate) {
            this.type = type;
            this.brand = brand;
            this.model = model;
            this.usdPrice = usdPrice;
            this.releaseDate = releaseDate;
        }
    }

    public static class PriceAdapter extends XmlAdapter<String, BigDecimal> {
        @Override
        public BigDecimal unmarshal(String v) {
            return new BigDecimal(v.replaceAll("[^\\d.]", ""));
        }
        @Override
        public String marshal(BigDecimal v) {
            return v != null ? String.format("$%,.2f", v) : "$0.00";
        }
    }
}

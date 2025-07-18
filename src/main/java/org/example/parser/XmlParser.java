package org.example.parser;

import org.example.model.Car;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

public class XmlParser implements FileParser<Car> {
    private static final String CURRENCY_ATTR = "currency";
    private static final String CAR_NODE = "car";
    private static final String TYPE_NODE = "type";
    private static final String MODEL_NODE = "model";
    private static final String PRICE_NODE = "price";
    private static final String PRICES_NODE = "prices";
    private static final String UNKNOWN_BRAND = "Unknown";

    private static final Map<String, String> MODEL_TO_BRAND = Map.ofEntries(
            Map.entry("rav4", "Toyota"),
            Map.entry("civic", "Honda"),
            Map.entry("f-150", "Ford"),
            Map.entry("330i", "Audi"),
            Map.entry("q5", "Audi"),
            Map.entry("silverado", "Chevrolet"),
            Map.entry("rogue", "Nissan"),
            Map.entry("elantra", "Hyundai")
    );

    @Override
    public List<Car> parse(File file) throws ParserException {
        Objects.requireNonNull(file, "Input file cannot be null");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            return parseCarNodes(document.getElementsByTagName(CAR_NODE));
        } catch (Exception e) {
            throw new ParserException("Failed to parse XML file: " + file.getName(), e);
        }
    }

    private List<Car> parseCarNodes(NodeList carNodes) {
        return IntStream.range(0, carNodes.getLength())
                .mapToObj(carNodes::item)
                .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                .map(node -> parseCarElement((Element) node))
                .filter(Objects::nonNull)
                .toList();
    }

    private Car parseCarElement(Element carElement) {
        try {
            String type = getElementText(carElement, TYPE_NODE);
            String model = getElementText(carElement, MODEL_NODE);
            String brand = determineBrand(model);
            Map<String, BigDecimal> prices = parsePrices(carElement);

            return new Car.Builder()
                    .type(type)
                    .brand(brand)
                    .model(model)
                    .prices(prices)
                    .build();
        } catch (Exception e) {
            // Log the error and skip malformed car entries
            System.err.println("Skipping malformed car element: " + e.getMessage());
            return null;
        }
    }

    private Map<String, BigDecimal> parsePrices(Element carElement) {
        Map<String, BigDecimal> prices = new HashMap<>();

        parsePriceElement(carElement, PRICE_NODE).ifPresent(prices::putAll);

        NodeList pricesNodes = carElement.getElementsByTagName(PRICES_NODE);
        if (pricesNodes.getLength() > 0) {
            NodeList priceNodes = ((Element) pricesNodes.item(0)).getElementsByTagName(PRICE_NODE);
            IntStream.range(0, priceNodes.getLength())
                    .mapToObj(priceNodes::item)
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .map(node -> parsePriceElement((Element) node))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(prices::putAll);
        }

        return Collections.unmodifiableMap(prices);
    }

    private Optional<Map<String, BigDecimal>> parsePriceElement(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return Optional.empty();
        }
        return parsePriceElement((Element) nodes.item(0));
    }

    private Optional<Map<String, BigDecimal>> parsePriceElement(Element priceElement) {
        try {
            String currency = priceElement.getAttribute(CURRENCY_ATTR);
            if (currency.isBlank()) {
                return Optional.empty();
            }

            BigDecimal price = new BigDecimal(priceElement.getTextContent().trim());
            return Optional.of(Map.of(currency, price));
        } catch (Exception e) {
            System.err.println("Invalid price element: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent().trim() : "";
    }

    private String determineBrand(String model) {
        if (model == null || model.isBlank()) {
            return UNKNOWN_BRAND;
        }

        String lowerModel = model.toLowerCase();
        return MODEL_TO_BRAND.entrySet().stream()
                .filter(entry -> lowerModel.contains(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseGet(() -> {
                    if (lowerModel.startsWith("model")) return "Tesla";
                    if (lowerModel.startsWith("c-")) return "Mercedes-Benz";
                    return UNKNOWN_BRAND;
                });
    }

    public static class ParserException extends Exception {
        public ParserException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
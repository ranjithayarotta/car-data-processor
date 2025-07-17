package org.example;

import org.example.factory.CarFilterFactory;
import org.example.factory.CarSortFactory;
import org.example.model.Car;
import org.example.model.CarBrand;
import org.example.output.JsonFormatter;
import org.example.output.OutputFormatter;
import org.example.output.TableFormatter;
import org.example.output.XmlFormatter;
import org.example.parser.CsvParser;
import org.example.parser.XmlParser;
import org.example.repository.BrandRepository;
import org.example.repository.CarRepository;
import org.example.repository.InMemoryBrandRepository;
import org.example.repository.InMemoryCarRepository;
import org.example.service.CarService;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class App {
    private OutputFormatter formatter;

    private final CarService carService;

    private final Scanner scanner;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public App(CarService carService,
               OutputFormatter formatter) {
        this.formatter = formatter;
        this.scanner = new Scanner(System.in);
        showFormatMenu();
        this.carService= carService;
    }

    public void run() {
        while (true) {
            displayMenu();
            int option = scanner.nextInt();
            scanner.nextLine(); // consume newline

            try {
                switch (option) {
                    case 1 -> filterByBrandAndPrice();
                    case 2 -> filterByBrandAndDate();
                    case 3 -> sortByReleaseDate();
                    case 4 -> sortByPrice();
                    case 5 -> sortByTypeCurrency();
                    case 6 -> { return; }
                    default -> System.out.println("Invalid option");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nCar Data Processor");
        System.out.println("1. Filter by Brand and Price");
        System.out.println("2. Filter by Brand and Release Date");
        System.out.println("3. Sort by Release Date");
        System.out.println("4. Sort by Price");
        System.out.println("5. Sort by Type-Specific Currency");
        System.out.println("6. Exit");
        System.out.print("Select option: ");
    }

    private void filterByBrandAndPrice() {
        System.out.print("Enter brand: ");
        String brand = scanner.nextLine();
        System.out.print("Enter min price: ");
        BigDecimal minPrice = new BigDecimal(scanner.nextLine());
        System.out.print("Enter max price: ");
        BigDecimal maxPrice = new BigDecimal(scanner.nextLine());
        List<Car> results = carService.filterCarsByBrandAndPrice(brand, minPrice, maxPrice);
        System.out.println("\nFiltered Results:");
        System.out.println(formatter.format(results));
    }

    private void filterByBrandAndDate() {
        System.out.print("Enter brand: ");
        String brand = scanner.nextLine();
        System.out.print("Enter start date (yyyy-MM-dd): ");
        LocalDate startDate = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
        System.out.print("Enter end date (yyyy-MM-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine(), DATE_FORMAT);
        List<Car> results = carService.filterByBrandAndReleaseDate(brand, startDate, endDate);
        System.out.println("\nFiltered Results:");
        System.out.println(formatter.format(results));
    }

    private void sortByReleaseDate() {
        List<Car> results = carService.sortCarsByReleaseDate();
        System.out.println("\nSorted by Release Date (newest first):");
        System.out.println(formatter.format(results));
    }

    private void sortByPrice() {
        List<Car> results = carService.sortCarsByPrice();
        System.out.println("\nSorted by Price (highest first):");
        System.out.println(formatter.format(results));
    }

    private void sortByTypeCurrency() {

        List<Car> results = carService.sortCarsByTypeAndCurrency();
        System.out.println("\nSorted by Type-Specific Currency:");
        System.out.println(formatter.format(results));
    }


    public static void main(String[] args) throws Exception {
        CsvParser csvParser = new CsvParser();
        XmlParser xmlParser = new XmlParser();
        File brandsFile = new File("src/main/resources/CarsBrand.csv");
        File carsFile = new File("src/main/resources/carsType.xml");
        if (!brandsFile.exists() || !carsFile.exists()) {
            System.err.println("Error: Required data files not found. Please ensure both CarsBrand.csv and carsType.xml exist in src/main/resources");
            System.exit(1);
        }
        List<CarBrand> brands = csvParser.parse(brandsFile);
        List<Car> cars = xmlParser.parse(carsFile);
        BrandRepository brandRepository = new InMemoryBrandRepository(brands);
        CarRepository carRepository = new InMemoryCarRepository(cars);
        CarFilterFactory carFilterFactory = new CarFilterFactory(brandRepository);
        CarSortFactory carSortFactory = new CarSortFactory(brandRepository);
        OutputFormatter formatter = new TableFormatter();
        CarService carService= new CarService(carFilterFactory,carRepository,carSortFactory);
        new App(carService,formatter).run();
    }

    private void showFormatMenu() {
        System.out.println("\nCurrent Output Format: " + getCurrentFormatName());
        System.out.println("Select Output Format:");
        System.out.println("1. Table Format (default)");
        System.out.println("2. JSON Format");
        System.out.println("3. XML Format");
        System.out.print("Choose format (1-3, or Enter to keep current): ");

        try {
            String input = scanner.nextLine();
            if (!input.isEmpty()) {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 2 -> this.formatter = new JsonFormatter();
                    case 3 -> this.formatter = new XmlFormatter();
                    case 1 -> this.formatter = new TableFormatter();
                    default -> System.out.println("Invalid choice, keeping current format");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping current format");
        }
    }

    private String getCurrentFormatName() {
        if (formatter instanceof JsonFormatter) return "JSON";
        if (formatter instanceof XmlFormatter) return "XML";
        return "Table (default)";
    }
}
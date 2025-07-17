
# Car Data Processor 

A Java application for filtering and sorting car data with multiple output format options.

---

## Features

- Filter cars by brand and price range
- Filter cars by brand and release date range
- Sort cars by release date (newest first)
- Sort cars by price (highest first)
- Sort cars by type and currency
- Multiple output formats (Table, JSON, XML)
- Interactive console menu
- Comprehensive test coverage

---

##  Test Coverage

This project uses [JaCoCo](https://www.jacoco.org/jacoco/) to generate code coverage reports for unit tests.

###  Coverage Summary

| Package                  | Line Coverage | Branch Coverage |
|--------------------------|---------------|-----------------|
| `org.example`            | 0%            | 0%              |
| `org.example.model`      | 52%           | 19%             |
| `org.example.output`     | 82%           | 64%             |
| `org.example.strategy`   | 90%           | 81%             |
| `org.example.factory`    | 0%            | N/A             |
| `org.example.parser`     | 94%           | 68%             |
| `org.example.service`    | 100%          | N/A             |
| `org.example.repository` | 100%          | 100%            |
| `org.example.exception`  | 100%          | N/A             |

**  Total Line Coverage: `72%`**  
** Total Branch Coverage: `60%`**

### 📁 Generate & View the Report

```bash
mvn clean test jacoco:report
```

Then open the following file in your browser:

```
target/site/jacoco/index.html
```

## 🛠 Installation

### Prerequisites

- Java JDK 20+
- Maven 3.6.0+

### Setup

```bash
git clone https://github.com/ranjithayarotta/car-data-processor
cd car-data-processor
mvn clean install
java -jar target/car-data-processor-1.0-SNAPSHOT.jar
```

---

##  Usage

On startup, select an output format. Then use the menu:

```
Current Output Format: Table (default)
Select Output Format:
1. Table Format (default)
2. JSON Format
3. XML Format
   Choose format (1-3, or Enter to keep current):
   ```

```
Car Data Processor
1. Filter by Brand and Price
2. Filter by Brand and Release Date
3. Sort by Release Date
4. Sort by Price
5. Sort by Type-Specific Currency
6. Exit
```

### Example Commands
```
1. Filter by Brand and Price
   Example: Find all BMW cars between $30,000 and $50,000

Select option: 1
Enter brand: BMW
Enter min price: 30000
Enter max price: 50000
Sample Output (Table Format):

+-------+------------+---------------+----------+--------+
| Brand | Model      | Release Date  | Price    | Currency |
+-------+------------+---------------+----------+--------+
| BMW   | 330i       | 2022-03-15    | 42900.00 | USD     |
| BMW   | X5         | 2021-11-20    | 48500.00 | USD     |
+-------+------------+---------------+----------+--------+
```
```
2. Filter by Brand and Release Date

Select option: 2
Enter brand: Honda
Enter start date (yyyy-MM-dd): 2020-01-01
Enter end date (yyyy-MM-dd): 2021-12-31
Output:

+-------+-------------+---------------+----------+---------+
| Brand | Model       | Release Date  | Price    | Currency|
+-------+-------------+---------------+----------+---------+
| Honda | Accord LX   | 2021-05-20    | 26500.00 | USD     |
| Honda | CR-V EX     | 2020-09-12    | 31200.00 | USD     |
+-------+-------------+---------------+----------+---------+
```
```
3. Sort by Release Date (Newest First)

Select option: 3
Output:
+--------+------------+---------------+----------+---------+
| Brand  | Model      | Release Date  | Price    | Currency|
+--------+------------+---------------+----------+---------+
| Tesla  | Model Y    | 2023-02-10    | 54990.00 | USD     |
| Ford   | Mustang    | 2022-12-05    | 42900.00 | USD     |
| BMW    | 330i       | 2022-03-15    | 42900.00 | USD     |
| ...    | ...        | ...           | ...      | ...     |
+--------+------------+---------------+----------+---------+

```
```
4. Sort by Price (Highest First)

Select option: 4
Output:

+----------+------------+---------------+-----------+---------+
| Brand    | Model      | Release Date  | Price     | Currency|
+----------+------------+---------------+-----------+---------+
| Porsche  | 911 Turbo  | 2022-06-18    | 127500.00 | USD     |
| Mercedes | S-Class    | 2021-09-22    | 112300.00 | USD     |
| Land Rover| Defender  | 2022-01-15    | 82500.00  | USD     |
| ...      | ...        | ...           | ...       | ...     |
+----------+------------+---------------+-----------+---------+

```
```
5. Sort cars by type and currency

Select option: 5
Output:

+------------+----------+--------+------------+-----------+
| Type       | Currency | Brand  | Model      |  Price    |
+------------+----------+--------+------------+-----------+
| SUV        | USD      | BMW    | X5         | 62500.00  |
| SUV        | EUR      | Volvo  | XC90       | 58900.00  |
| Sedan      | USD      | Audi   | A6         | 54900.00  |
| Electric   | USD      | Tesla  | Model 3    | 42900.00  |
| Truck      | USD      | Ford   | F-150      | 38500.00  |
+------------+----------+--------+------------+-----------+


```

- **Change output to JSON**:
  ```
  When prompted at startup, enter 2
  ```

---

##  Data Files

- `src/main/resources/CarsBrand.csv`
- `src/main/resources/carsType.xml`

### Sample Data

**CarsBrand.csv**
```csv
"Brand,ReleaseDate"
"Toyota,01/15/2023"
"Honda,11/20/2022"
```

**carsType.xml**
```xml
    <car>
   <type>SUV</type>
   <model>RAV4</model>
   <price currency="USD">25000.00</price>
   <prices>
      <price currency="EUR">23000.00</price>
      <price currency="GBP">20000.00</price>
      <price currency="JPY">2800000.00</price>
   </prices>
</car>
```

---

## 🧪 JaCoCo Plugin Setup

Make sure your `pom.xml` includes the following configuration:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
      <version>0.8.8</version>
      <executions>
        <execution>
          <goals>
            <goal>prepare-agent</goal>
          </goals>
        </execution>
        <execution>
          <id>report</id>
          <phase>test</phase>
          <goals>
            <goal>report</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
```

---

## 🛡 License

MIT License – See the [LICENSE](LICENSE) file for details.

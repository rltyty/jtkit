# jtkit
A JUnit 5+ testing toolkit that simplifies test data loading and test case
setup through declarative annotations.

## Installation

Add to your project `pom.xml`:

```xml
<dependency>
  <groupId>io.github.rltyty</groupId>
  <artifactId>jtkit</artifactId>
  <version>0.7.0</version>
  <scope>test</scope>
</dependency>
```

## Writing Test Cases

1. Extend `BaseTest`.
2. Define a test data structure as a Java record that implements the
   `Scenario` interface, e.g., `SortData`. The record provides implementations
   of `toStr()` and `arr2Str()`.
3. **`FlatFileLoader`**: Within a test class, each test data file is loaded
   and parsed only once. Parsed scenarios are cached using a composite cache
   key consisting of the file's absolute path and the corresponding record
   type.
4. Use the `@FileData` annotation to specify the test data file's location and
   the loader to use for a test method.
5. A test method takes a single argument of type `Scenario`. The actual
   argument is an instance of the record defined in step 2, allowing the
   loader to parse each test case into the corresponding record type.

**NOTE**:

a. Each test scenario is parsed only once from the data source and stored in a
cache maintained for the lifetime of the test class.

b. Every scenario object passed to a test method is a deep copy of the
original object stored in the cache.

c. Testers may choose to perform the initial data loading in `setUpAll()`
before any tests are executed. This is known as **cache warm-up**. It is
useful when measuring and comparing the execution times of different
algorithms using the same data, as it prevents data loading time from
affecting the profiling results.

```java
@Timeout(value = 2, unit = TimeUnit.SECONDS)
public class SortingTest extends BaseTest {

  public record SortData(
      @ParseWith(DataType.LIST_INTEGER) List<Integer> input,
      @ParseWith(DataType.BOOLEAN)      boolean reverse) implements Scenario {
  }

  @BeforeAll
  public static void setUpAll() { // Warm the cache
      new FlatFileLoader<>(SortData.class) .load("small.dat", SortingTest.class);
      new FlatFileLoader<>(SortData.class) .load("large.dat", SortingTest.class);
  }

  @FileData(path = "small.dat", loader = FlatFileLoader.class)
  public void test_insertion_sort(SortData data) {
    captureTestCase(data);
    Comparator<Integer> cmp =
        data.reverse() ? Comparator.reverseOrder() : Comparator.naturalOrder();
    List<Integer> expected = new ArrayList<>(data.input());
    expected.sort(cmp);
    Sorting.insertionSort(data.input(), cmp);
    assertEquals(expected, data.input());
  }

  @FileData(path = "large.dat", loader = FlatFileLoader.class)
  public void test_baseline(SortData data) {
    captureTestCase(data);
    Comparator<Integer> cmp =
        data.reverse() ? Comparator.reverseOrder() : Comparator.naturalOrder();
    data.input().sort(cmp);
  }

  @FileData(path = "large.dat", loader = FlatFileLoader.class)
  public void test_quicksort(SortData data) {
    captureTestCase(data);
    Comparator<Integer> cmp =
        data.reverse() ? Comparator.reverseOrder() : Comparator.naturalOrder();
    List<Integer> expected = new ArrayList<>(data.input());
    expected.sort(cmp);
    Sorting.quickSort(data.input(), cmp);
    assertEquals(expected, data.input());
  }

}
```

## Running test cases

### Profile test cases

```shell
mvn test -Dtest.timer.enabled=true -Dtest=SortingTest
```


### Enable long running cases

```shell
mvn test -Dtest.long.running.tests.enabled=true -Dtest=SortingTest
```

### Filter test case by name

```shell
mvn test -Dtest="SortingTest#*baseline"
```

## Requirements

- Java 17 or later
- JUnit Jupiter 5.13.3 or later

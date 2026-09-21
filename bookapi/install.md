# Book API Test Automation

BDD API test automation for the [Simple Books API](http://simple-books-api.glitch.me/books), using Cucumber + REST Assured on top of JUnit 5 (JUnit Platform Suite).

## Prerequisites

- Java 25+
- Maven 3.8+
- Network access to `simple-books-api.glitch.me` (tests call the live API)

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/jjphone/cs-bookapi.git
   cd cs-bookapi/bookapi
   ```

2. Install dependencies and compile (downloads Maven dependencies on first run):

   ```bash
   mvn test-compile
   ```

3. Verify the setup by running the test suite:

   ```bash
   mvn test
   ```

## Project layout

```
src/test/java/cs/runner/            Cucumber JUnit Platform Suite runner (RunCucumberTest)
src/test/java/cs/step_definitions/  Step definitions
src/test/java/cs/utils/             Helpers (param parsing, response attribute comparisons)
src/test/resources/features/        Gherkin feature files
```

## Running the tests

Run the full suite:

```bash
mvn test
```

## Running tests by tag

Scenarios in `BooksList.feature` are tagged `@smoke` and `@regression`. Filter which ones run with the `cucumber.filter.tags` system property:

```bash
# Smoke tests only
mvn test -Dcucumber.filter.tags="@smoke"

# Regression tests only
mvn test -Dcucumber.filter.tags="@regression"

# Either tag
mvn test -Dcucumber.filter.tags="@smoke or @regression"

# Regression, excluding anything also tagged @wip
mvn test -Dcucumber.filter.tags="@regression and not @wip"
```

`cucumber.filter.tags` is read by the `cucumber-junit-platform-engine` from JVM system properties, and Maven Surefire forwards `-D` values from the command line into the forked test JVM, so no extra configuration is needed.

### Setting a default tag filter

To make a tag filter apply by default (without passing `-D` every time), add a `@ConfigurationParameter` to `src/test/java/cs/runner/RunCucumberTest.java`:

```java
import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;

@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@smoke")
```

A `-Dcucumber.filter.tags` passed on the command line still overrides this default.


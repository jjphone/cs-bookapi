# cs-bookapi

API test automation for the [Simple Books API](http://simple-books-api.glitch.me/books) `GET /books` endpoint.

## What this repo does

- Sends a `GET` request to `http://simple-books-api.glitch.me/books` and validates the response.
- Built with **Java**, **REST Assured**, and **Cucumber**, following a **BDD** test approach.
- All validations are declared in the Gherkin feature file ([bookapi/src/test/resources/features/BooksList.feature](bookapi/src/test/resources/features/BooksList.feature)), not hard-coded in Java — properties and conditions to check can be added or updated there directly, without touching the step definitions.

## Test coverage

The feature file has **2 scenarios**, each with **2 sets of examples** tagged for different test runs:

| Scenario | What it checks | Tags |
|---|---|---|
| Response header attributes | HTTP status code, response time, response size, content-type, and freshness of the `Date` header | `@smoke` (status code only), `@regression` (all attributes) |
| Response content | Navigates the JSON array and asserts specific books/properties exist (or don't) by key/value, including an `{notEmpty}` placeholder for "field must be present and non-blank" checks | `@smoke` (single check), `@regression` (broader set) |

## Project layout

```
bookapi/                              Maven project
  src/test/resources/features/        Gherkin feature files (test cases)
  src/test/java/cs/step_definitions/  Cucumber step definitions
  src/test/java/cs/utils/             Helpers (param parsing, response attribute comparisons)
  src/test/java/cs/runner/            Cucumber JUnit Platform Suite runner
```

See [bookapi/README.md](bookapi/README.md) for prerequisites, installation, and how to run the tests (including running by `@smoke`/`@regression` tag).

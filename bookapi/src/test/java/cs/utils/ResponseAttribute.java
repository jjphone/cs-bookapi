package cs.utils;

import io.restassured.response.Response;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ResponseAttribute {

    public static String getAttributeText(String name, Response response) {
        switch (name) {
            case "status":
                return Integer.toString(response.getStatusCode());
            case "time":
                ZonedDateTime responseDate = ZonedDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);
                return responseDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            case "type":
                return response.getHeader("Content-Type");
            case "size":
                return response.getHeader("Content-Length");
            default:
                throw new IllegalArgumentException("Unknown attribute name: " + name);
        }
    }


    public static boolean compareAttributes(HashMap<String, String> item, Response response) {
        String field = item.get("field");
        String actual = getAttributeText(field, response);
        String operator = item.get("operator");
        String expected = item.get("value");
        

        return compare(actual, operator, expected);
    }

    private static boolean compare(String actual, String operator, String expected) {
        int comparison = compareValues(actual, expected);

        switch (operator) {
            case "=":
                return comparison == 0;
            case "!=":
                return comparison != 0;
            case ">":
                return comparison > 0;
            case ">=":
                return comparison >= 0;
            case "<":
                return comparison < 0;
            case "<=":
                return comparison <= 0;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static int compareValues(String actual, String expected) {
        try {
            return new BigDecimal(actual.trim()).compareTo(new BigDecimal(expected.trim()));
        } catch (NumberFormatException exception) {
            return actual.compareTo(expected);
        }
    }


}

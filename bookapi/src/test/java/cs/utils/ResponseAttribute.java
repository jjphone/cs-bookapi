package cs.utils;

import io.restassured.response.Response;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class ResponseAttribute {

    public static String getAttributeText(String name, Response response) {
        String result = null;
        switch (name) {
            case "status":
            case "status code":
                result = Integer.toString(response.getStatusCode());
                break;
            
            case "time":
                result = Long.toString(response.getTimeIn(java.util.concurrent.TimeUnit.MILLISECONDS));
                break;

            case "date":
                ZonedDateTime responseDate = ZonedDateTime.parse(response.getHeader("Date"), DateTimeFormatter.RFC_1123_DATE_TIME);
                result = responseDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                break;

            case "type":
            case "content-type":
                result = response.getHeader("Content-Type");
                break;

            case "size":
            case "content-length":
                result = response.getHeader("Content-Length");
                break;

            default:
                throw new IllegalArgumentException("Unknown attribute name: " + name);
        }
        return result;
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

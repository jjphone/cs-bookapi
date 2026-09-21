package cs.utils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.ZonedDateTime;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class ParamHelper {

    // part 1: letters only (field name), part 2: special chars (operator), part 3: numbers or letters mixed with numbers/special chars (value)
    private static final Pattern PARAM_PATTERN = Pattern.compile("^([A-Za-z -]+)\\s*([=!<>]+)\\s*(.+)$");

    public static String[] textToList(String paramName) {
        final String DELIM = ",";
        return textToList(paramName, DELIM);

    }

    public static String[] textToList(String text, String listDelimiter) {
        return text.split(listDelimiter);
    }


    // convert dataText to dynamic data text in ISO_LOCAL_DATE_TIME
    // the date text can be in the following formats: {now()-1day}, {now()-1hour}, {now()-1minute} or just 2026-01-01 01:15:00
    private static String calculateDateValue(String dateText){
        if (!dateText.contains("now()")) 
            return dateText;

        //dataText should be in now(), or now()-1day or now()+1hour format.
        String[] parts = dateText.split("\\d+");
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime res;
        if (parts.length < 2) { // only now() is present, return current date in ISO_LOCAL_DATE format
            return now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        int amount = Integer.parseInt(dateText.replaceAll("[^\\d]", ""));
        parts[1] = parts[1].substring(0, parts[1].length() - 1); // remove the last character of '}'  
        switch (parts[1]) {

            case "day":
            case "days":
                res = parts[0].contains("-") ? now.minusDays(amount) : now.plusDays(amount);
                break;
            case "hour":
            case "hours":
                res = parts[0].contains("-") ? now.minusHours(amount) : now.plusHours(amount);
                break;
            case "minute":
            case "minutes":
                res = parts[0].contains("-") ? now.minusMinutes(amount) : now.plusMinutes(amount);
                break;
            default:
                throw new IllegalArgumentException("Invalid date format: " + dateText);
        }
        return res.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, String>[] parseParams(String text) {
        String[] params = textToList(text);
        HashMap<String, String>[] parsedParams = (HashMap<String,String>[]) new HashMap[params.length];

        for (int i = 0; i < params.length; i++) {
            Matcher matcher = PARAM_PATTERN.matcher(params[i].trim());

            if (!matcher.matches()) {
                throw new IllegalArgumentException("Unable to parse parameter: '" + params[i] + "'");
            }

            HashMap<String, String> item = new HashMap<String, String>();
            item.put("field", matcher.group(1).toLowerCase().trim());
            item.put("operator", matcher.group(2));
            

            if ( item.get("field").equals("time") ) {
                item.put("value", calculateDateValue(matcher.group(3)));
            }else {
                item.put("value", matcher.group(3));
            }


            item.put("reason", "header type mismatch for condition: " + params[i]);
            parsedParams[i] = item;
        }
        return parsedParams;
    }

    public static HashMap<String, String> textToHash(String text) {
        // Convert the text to a HashMap object
        HashMap<String, String> hashMap = new HashMap<>();
        String[] params = textToList(text);

        for (String param : params) {
            String[] keyValue = param.split("=", 2);
            if (keyValue.length == 2) {
                hashMap.put(keyValue[0].trim(), keyValue[1].trim());
            } else {
                throw new IllegalArgumentException("Invalid parameter format: " + param);
            }
        }
        return hashMap; 
    }

    public static boolean matchesContainProperties(HashMap<String, String> item, HashMap<String, String> containProperties) {
        final String NOT_EMPTY_PLACEHOLDER = "{notEmpty}";
        final String EMPTY_PLACEHOLDER = "{empty}";
        final String STARTS_BRACKET_PLACEHOLDER = "{";
        final String ENDS_BRACKET_PLACEHOLDER = "}";

        for (Map.Entry<String, String> entry : containProperties.entrySet()) {
            
            String expectedValue = entry.getValue().trim();
            Object actualValue = item.get(entry.getKey());

            // expected: isempty  - return false when actualValue is not empty
            if(expectedValue.equals(EMPTY_PLACEHOLDER) ){ 
                if (actualValue!=null && !actualValue.toString().isEmpty()) 
                    return false;
            }

            // expected: not empty - return false when actual value is null or empty, actual value does not matter
            if(expectedValue.equals(NOT_EMPTY_PLACEHOLDER) ){ 
                if (actualValue==null || actualValue.toString().isEmpty()) 
                    return false;
            }

            // perform actual value comparison
            if( !(expectedValue.startsWith(STARTS_BRACKET_PLACEHOLDER) && expectedValue.endsWith(ENDS_BRACKET_PLACEHOLDER)) ) { 
                if (actualValue == null || !actualValue.toString().equals(expectedValue)) {
                    return false;
                }
            }

        }
        return true;
    }



    public static boolean matchesNotContainProperties(HashMap<String, String> item, HashMap<String, String> containProperties) {
        final String NOT_EMPTY_PLACEHOLDER = "{notEmpty}";
        final String EMPTY_PLACEHOLDER = "{empty}";
        final String STARTS_BRACKET_PLACEHOLDER = "{";
        final String ENDS_BRACKET_PLACEHOLDER = "}";

        for (Map.Entry<String, String> entry : containProperties.entrySet()) {
            
            String expectedValue = entry.getValue().trim();
            Object actualValue = item.get(entry.getKey());

            // expected: isempty  - return false when actualValue is empty
            if(expectedValue.equals(EMPTY_PLACEHOLDER) ){ 
                if(actualValue == null || actualValue.toString().isEmpty())
                    return false;
            }

            // expected: not empty - return false when actual value is not empty, actual value does not matter
            if(expectedValue.equals(NOT_EMPTY_PLACEHOLDER) ){ 
                if (actualValue !=null && !actualValue.toString().isEmpty()) 
                    return false;
            }

            // perform actual value comparison
            if( !(expectedValue.startsWith(STARTS_BRACKET_PLACEHOLDER) && expectedValue.endsWith(ENDS_BRACKET_PLACEHOLDER)) ) { 
                if (actualValue != null && actualValue.toString().equals(expectedValue)) {
                    return false;
                }
            }

        }
        return true;
    }


    public static List<HashMap<String, String>> filterResponseJson(Response response, String filterText, boolean filterMatched) {
        HashMap<String, String> containProperties = ParamHelper.textToHash(filterText);
        JsonPath responseJson = response.jsonPath();
        
        //extract hashmap of response json
        List<Map<String, String>> books = responseJson.getList("$");
        List<HashMap<String, String>> items = new ArrayList<>();
        for (Map<String, String> book : books) {
            items.add(new HashMap<>(book));
        }

        List<HashMap<String, String>> matchedItems = new ArrayList<>();
        for (HashMap<String, String> item : items) {
            boolean isMatched = filterMatched ?  matchesContainProperties(item, containProperties) : matchesNotContainProperties(item, containProperties);
            if (isMatched)
                matchedItems.add(item);
        }
        return matchedItems;
    }

}

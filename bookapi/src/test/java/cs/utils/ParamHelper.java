package cs.utils;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.ZonedDateTime;

public class ParamHelper {

    // part 1: letters only (field name), part 2: special chars (operator), part 3: numbers or letters mixed with numbers/special chars (value)
    private static final Pattern PARAM_PATTERN = Pattern.compile("^([A-Za-z]+)\\s*([=!<>]+)\\s*(.+)$");

    public static String[] textToList(String paramName) {
        final String DELIM = ",";
        return textToList(paramName, DELIM);

    }

    public static String[] textToList(String text, String listDelimiter) {
        return text.split(listDelimiter);
    }


    // convert dataText to dynamic data text in ISO_LOCAL_DATE_TIME
    // the date text can be in the following formats: now()-1day, now()-1hour, now()-1minute or just 2026-01-01 01:15:00
    private static String calculateDateValue(String dateText){
        if (!dateText.startsWith("now()")) 
            return dateText;

        //dataText should be in now(), or now()-1day or now()+1hour format.
        String[] parts = dateText.split("\\d+");
        
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime res;
        if (parts.length < 2) { // only now() is present, return current date in ISO_LOCAL_DATE format
            return now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        int amount = Integer.parseInt(dateText.replaceAll("[^\\d]", ""));
        
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
            item.put("field", matcher.group(1));
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



}

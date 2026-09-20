package cs.utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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



    private static HashMap<String, String> parseParam(String param) {
        Matcher matcher = PARAM_PATTERN.matcher(param.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Unable to parse parameter: '" + param + "'");
        }

        HashMap<String, String> parsed = new HashMap<>();
        parsed.put("field", matcher.group(1));
        parsed.put("operator", matcher.group(2));
        parsed.put("value", matcher.group(3));
        return parsed;
    }



}

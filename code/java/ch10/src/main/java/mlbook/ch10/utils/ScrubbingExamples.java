package mlbook.ch10.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScrubbingExamples {

    private final static String text = "You are worthy, you are family. Focus on what you have to be grateful for.";

    public String convertToLowerCase(String in) {
        return in.toLowerCase();
    }

    public String removePuncuation(String in) {
        String patternString = "[\\w]+";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(convertToLowerCase(in));
        StringBuilder sb = new StringBuilder();

        while(matcher.find()) {
            sb.append(matcher.group() + " ");
        }
        return sb.toString().trim();
    }


    public static void main(String[] args) {
        ScrubbingExamples s = new ScrubbingExamples();
        System.out.println(s.removePuncuation(text));
    }








}

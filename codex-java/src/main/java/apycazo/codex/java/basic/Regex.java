package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Regex {

  public static void main(String[] args) {
    Regex regex = new Regex();
    log.info("--- test to extract values from strings");
    Arrays
      .stream(new String[]{"(1)", "test(2)", "(3)value", "test(4)value", "()", "", " a b c (7) d e f ", "(x) (8)"})
      .forEach(text -> log.info("Source: '{}', Value: '{}'", text, regex.extractValue(text)));
    log.info("--- test to operate with values");
    Arrays
      .stream(new String[]{"plus(9)", "minus(11)", "zero(x)"})
      .forEach(text -> log.info("Text: '{}', Value: '{}'", text, regex.extractNumber(text)));
  }

  public String extractValue(String text) {
    Pattern pattern = Pattern.compile("\\w?\\((.*?)\\)\\w?");
    Matcher matcher = pattern.matcher(text);
    String group = "";
    while (matcher.find()) {
      group = matcher.group(1);
    }
    return group;
  }

  public int extractNumber(String text) {
    Pattern minus = Pattern.compile("minus\\((.*?)\\)");
    Pattern plus = Pattern.compile("plus\\((.*?)\\)");
    Matcher matcher = minus.matcher(text);
    if (matcher.find()) {
      String rawValue = matcher.group(1);
      int number = Integer.parseInt(rawValue);
      return --number;
    } else if ((matcher = plus.matcher(text)).find()) {
      String rawValue = matcher.group(1);
      int number = Integer.parseInt(rawValue);
      return ++number;
    } else return 0;
  }
}

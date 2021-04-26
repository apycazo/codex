package apycazo.codex.java.basic;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Collections {

  public static void main(String[] args) {
    Object[] objArray = new Object[] {"first", "second"};
    String[] stringArray = Arrays.copyOf(objArray, objArray.length, String[].class);
    log.info("String array content");
    for (String s : stringArray) log.info("V: {}", s);
  }
}

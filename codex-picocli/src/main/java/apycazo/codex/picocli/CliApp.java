package apycazo.codex.picocli;

import picocli.CommandLine;

public class CliApp {

  public static void main(String... args) {
//    new CommandLine(new TopCommand()).usage(System.out);
    new CommandLine(new TopCommand()).execute(args);
  }
}

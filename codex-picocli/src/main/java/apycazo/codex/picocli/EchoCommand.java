package apycazo.codex.picocli;

import picocli.CommandLine;

@CommandLine.Command(name = "echo", description = "Echoes provided text")
public class EchoCommand implements Runnable {

  @CommandLine.Option(names = {"-t", "--text"}, required = true, description = "Text to echo")
  private String text;

  @Override
  public void run() {
    System.out.println("echo :: " + text);
  }
}

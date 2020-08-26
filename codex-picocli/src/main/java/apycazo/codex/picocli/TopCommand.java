package apycazo.codex.picocli;

import picocli.CommandLine;

/**
 * This command is only meant to group multiple commands, its name seems to be irrelevant.
 * Usage samples: <br>
 * <ul>
 *   <li>Show help: java -jar pico.jar -h</li>
 *   <li>Date: java -jar pico.jar date</li>
 *   <li>Date (with options): java -jar pico.jar date -z=GMT</li>
 * </ul>
 */
@CommandLine.Command(name = "top", subcommands = {
  DateCommand.class, EchoCommand.class
})
public class TopCommand implements Runnable {

  @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Print usage help and exit.")
  boolean usageHelpRequested;

  @Override
  public void run() {
    new CommandLine(this).usage(System.out);
  }

  @CommandLine.Command(name = "test", description = "show a test message")
  public void test() {
    System.out.println("Testing OK!");
  }
}

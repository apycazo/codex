package apycazo.codex.k8s.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServiceProperties {

  private static final Logger log =
    LoggerFactory.getLogger(ServiceProperties.class);

  private final String dbhost;
  private final String dbuser;
  private final String dbpass;
  private final int port;

  public ServiceProperties(String filePath, String[] args) {
    Properties properties = null;
    if (filePath != null && !filePath.isEmpty()) {
      try (InputStream input = new FileInputStream(filePath)) {
        properties = new Properties();
        properties.load(input);
      } catch (IOException ex) {
        log.error("Failed to read properties from file '{}'", filePath);
      }
    } else {
      log.info("No properties defined at '{}'", filePath);
    }
    if (properties != null) {
      dbhost = properties.getProperty("dbhost", "");
      dbuser = properties.getProperty("dbuser", "root");
      dbpass = properties.getProperty("dbpass", "");
      port = Integer.parseInt(properties.getProperty("port", "8080"));
    } else {
      Map<String, String> map;
      if (args != null && args.length > 0) {
        map = parseArgs(args);
      } else {
        map = Collections.emptyMap();
      }
      // set properties taking values from the generated map
      dbhost = map.getOrDefault("dbhost", "");
      dbuser = map.getOrDefault("dbuser", "root");
      dbpass = map.getOrDefault("dbpass", "");
      port = Integer.parseInt(map.getOrDefault("port", "8080"));
    }
  }

  private Map<String, String> parseArgs(String [] args) {
    Map<String, String> map = new HashMap<>();
    if (args == null || args.length == 0) {
      return map;
    }
    for (String entry : args) {
      String[] split = split(entry);
      map.put(split[0], split[1]);
    }
    return map;
  }

  private String[] split(String arg) {
    if (arg == null || arg.isEmpty()) {
      return new String[]{"", ""};
    } else {
      String[] split = arg.split("=");
      String key = split.length > 0 ? split[0].trim() : "";
      String value = split.length > 1 ? split[1].trim() : "";
      return new String[] {key, value};
    }
  }

  public String getDbhost() {
    return dbhost;
  }

  public String getDbuser() {
    return dbuser;
  }

  public String getDbpass() {
    return dbpass;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return String.format("dbhost: %s%ndbuser:%s%ndbpass:%s%nport:%d%n",
      dbhost, dbuser, dbpass, port);
  }
}

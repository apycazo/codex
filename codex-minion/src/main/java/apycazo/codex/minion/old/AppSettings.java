package apycazo.codex.minion.old;

import apycazo.codex.minion.common.CommonUtils;

import java.util.Properties;
import java.util.stream.Stream;

public class AppSettings {

  // base application properties
  private String name = "service";
  private int httpPort = 8080;
  // extra application properties
  private Properties properties;

  protected AppSettings() {}

  public static AppSettings parse(String serverSettingsConfig, String... applicationPropertiesLocations) {
    AppSettings settings = new AppSettings();
    if (serverSettingsConfig != null && !serverSettingsConfig.isEmpty()) {
      CommonUtils.readPropertiesFrom(serverSettingsConfig)
      .ifPresent(properties -> {
        settings.name = properties.getOrDefault("application.name", settings.name).toString();
        CommonUtils
          .readInt(properties.getOrDefault("application.http.port", settings.httpPort))
          .ifPresent(port -> settings.httpPort = port);
      });
    }
    if (applicationPropertiesLocations != null) {
      Stream.of(applicationPropertiesLocations).forEach(location -> {
        CommonUtils.readPropertiesFrom(location).ifPresent(readProps -> settings.properties.putAll(readProps));
      });
    }
    return settings;
  }

}

package apycazo.codex.minion.common;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonUtilsTests {

  @Test
  void readPropertiesFromClassPath() {
    String path = "classpath:classpath.properties";
    Optional<Properties> propertiesOptional = CommonUtils.readPropertiesFrom(path);
    assertThat(propertiesOptional).isNotNull();
    assertThat(propertiesOptional.isPresent()).isTrue();
    Properties props = propertiesOptional.get();
    assertThat(props.get("origin")).isEqualTo("test-resources");
  }

  @Test
  void readPropertiesFromFileSystem() {
    String path = "file:" + System.getProperty("user.dir") + "/local/fs.properties";
    Optional<Properties> propertiesOptional = CommonUtils.readPropertiesFrom(path);
    assertThat(propertiesOptional).isNotNull();
    assertThat(propertiesOptional.isPresent()).isTrue();
    Properties props = propertiesOptional.get();
    assertThat(props.get("origin")).isEqualTo("fileSystem");
  }
}

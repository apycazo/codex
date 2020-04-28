package apycazo.codex.micronaut.config;

import io.micronaut.context.annotation.ConfigurationProperties;

// note: micronaut works in compile-time, so lombok can not be used here
@ConfigurationProperties("app")
public class About {

  private String author;
  private String version;

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}

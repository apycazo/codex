package es.asgarke.golem.tools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class StringValueTest {

  @Test
  void test_path_join() {
    assertThat(StringValue.joinPaths()).isEqualTo("");
    assertThat(StringValue.joinPaths("test", "value")).isEqualTo("/test/value");
    assertThat(StringValue.joinPaths("/test", "/value")).isEqualTo("/test/value");
    assertThat(StringValue.joinPaths("/test/", "/value/")).isEqualTo("/test/value");
    assertThat(StringValue.joinPaths("http://test/", "/value/")).isEqualTo("http://test/value");
  }

  @Test
  void test_segment_resolver() throws URISyntaxException {
    String path = StringValue.joinPaths("/api/v2/:id");
    String[] segments = path.substring(1).split("/");
    for (String s : segments) {
      log.info("Segment: {}", s);
    }
    URI uri = new URI(path);
    log.info("URI: {}", uri.toString());
    log.info("URI: {}", uri.getPath());
    uri = new URI("http://test:9090/info");
    log.info("URI: {}", uri.getPath());
  }

}
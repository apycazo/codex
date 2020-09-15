package apycazo.codex.jersey.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Slf4j
@Service
public class DemoService {

  public String ts() {
    return "ts: " + Instant.now().toEpochMilli();
  }

  public InfoResource getInfo() {
    return InfoResource.builder().msg("codex-jersey").build();
  }
}

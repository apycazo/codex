package apycazo.codex.jetty.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Inner class to ensure a spring service is loaded.
 */
@Slf4j
@Service
public class SpringService {

  public SpringService() {
    log.info("Instanced spring service");
  }

  public String getName() {
    return SpringService.class.getName();
  }
}

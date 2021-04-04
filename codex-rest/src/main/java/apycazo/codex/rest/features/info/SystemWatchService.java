package apycazo.codex.rest.features.info;

import apycazo.codex.rest.common.status.StatusReport;
import apycazo.codex.rest.common.status.StatusReporter;
import apycazo.codex.rest.common.status.StatusValue;
import org.springframework.stereotype.Service;

@Service
public class SystemWatchService implements StatusReporter {

  private boolean isOk = true;

  @Override
  public StatusReport getReport() {
    synchronized (this) {
      if (isOk) {
        isOk = false; // next call I want the opposite result
        return StatusReport.builder()
          .status(StatusValue.OK)
          .msg("Service is working fine")
          .name(getClass().getSimpleName())
          .build();
      } else {
        isOk = true;
        return StatusReport.builder()
          .status(StatusValue.ERROR)
          .msg("Service is not working properly")
          .name(getClass().getSimpleName())
          .build();
      }
    }
  }
}

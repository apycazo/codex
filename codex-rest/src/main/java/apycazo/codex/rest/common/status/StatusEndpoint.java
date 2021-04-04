package apycazo.codex.rest.common.status;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.List;

@Slf4j
public class StatusEndpoint {

  private final List<StatusReporter> statusReporterList;
  private final SystemStatusInfo info;

  @Autowired
  public StatusEndpoint(List<StatusReporter> statusReporterList) {
    this.statusReporterList = statusReporterList;
    this.info = SystemStatusInfo.builder()
      .startedMillis(Instant.now().toEpochMilli())
      .reportingCount(statusReporterList.size())
      .build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SystemStatusInfo get() {
    return update();
  }

  private synchronized SystemStatusInfo update() {
    info.clear();
    for (StatusReporter reporter : statusReporterList) {
      info.register(reporter.getReport());
    }
    return info;
  }

}

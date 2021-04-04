package apycazo.codex.rest.common.status;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "status", "startedMillis", "reportingCount", "okCount", "unstableCount",
  "errorCount", "reports"})
public class SystemStatusInfo {

  @Builder.Default
  private long startedMillis = Instant.now().toEpochMilli();
  @Builder.Default
  private int reportingCount = 0;
  @Builder.Default
  private int okCount = 0;
  @Builder.Default
  private int unstableCount = 0;
  @Builder.Default
  private int errorCount = 0;
  @Builder.Default
  private List<StatusReport> reports = new ArrayList<>();

  public StatusValue getStatus() {
    if (okCount == reportingCount) {
      return StatusValue.OK;
    } else if (errorCount == 0) {
      return StatusValue.UNSTABLE;
    } else {
      return StatusValue.ERROR;
    }
  }

  public void clear() {
    okCount = 0;
    unstableCount = 0;
    errorCount = 0;
    reports.clear();
  }

  public void register(StatusReport report) {
    increment(report.getStatus());
    reports.add(report);
  }

  public void increment(StatusValue value) {
    if (value != null) {
      switch (value) {
        case OK:
          okCount++;
          break;
        case UNSTABLE:
          unstableCount++;
          break;
        case ERROR:
          errorCount++;
          break;
      }
    }
  }
}

package apycazo.codex.minion.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import static apycazo.codex.minion.server.ServerConstants.MEDIA_TYPE_JSON;
import static apycazo.codex.minion.server.ServerConstants.MEDIA_TYPE_TEXT_PLAIN;

@Data
@Builder
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
public class ServerResponse {

  @Builder.Default
  private int status = 200;
  @Builder.Default
  private Object body = null;
  @Builder.Default
  private String mediaType = MEDIA_TYPE_TEXT_PLAIN;

  public ServerResponse ok() {
    return ServerResponse.builder().build();
  }

  public ServerResponse json(Object content) {
    return ServerResponse.builder().body(body).mediaType(MEDIA_TYPE_JSON).build();
  }

  public ServerResponse notFound() {
    return ServerResponse.builder().build();
  }

  public ServerResponse clientError() {
    return ServerResponse.builder().status(400).build();
  }

  public ServerResponse serverError() {
    return ServerResponse.builder().status(500).build();
  }
}

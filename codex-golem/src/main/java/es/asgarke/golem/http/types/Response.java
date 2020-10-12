package es.asgarke.golem.http.types;

import es.asgarke.golem.http.definitions.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

  @Builder.Default
  public int status = 200;
  public Object content;
  public String mediaType;

  public static Response ok() {
    return Response.builder()
      .status(200)
      .build();
  }

  public static Response notFound() {
    return Response.builder()
      .status(404)
      .mediaType(MediaType.TEXT_PLAIN)
      .build();
  }

  public static Response internalServerError() {
    return Response.builder()
      .status(500)
      .mediaType(MediaType.TEXT_PLAIN)
      .build();
  }

  public static Response internalServerError(Throwable e) {
    return Response.builder()
      .status(500)
      .content(e.getMessage())
      .mediaType(MediaType.TEXT_PLAIN)
      .build();
  }

  public static Response requestError(Throwable e) {
    return Response.builder()
      .status(400)
      .content(e.getMessage())
      .mediaType(MediaType.TEXT_PLAIN)
      .build();
  }

  public static Response mediaTypeError(String media) {
    String msg = String.format("Invalid media type, required '%s'", media);
    return Response.builder()
      .status(415)
      .content(msg)
      .mediaType(MediaType.TEXT_PLAIN)
      .build();
  }
}

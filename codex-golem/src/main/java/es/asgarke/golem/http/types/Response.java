package es.asgarke.golem.http.types;

import com.sun.net.httpserver.Headers;
import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.tools.StringValue;
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
  private int status = 200;
  private Object content;
  private String mediaType;
  @Builder.Default
  private Headers headers = new Headers();

  public static Response status(int status) {
    return Response.builder()
      .status(status)
      .build();
  }

  public static Response ok() {
    return Response.builder()
      .status(200)
      .build();
  }

  public static Response ok(Object entity) {
    return Response.builder()
      .status(200)
      .content(entity)
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

  public Response withContent(String content) {
    this.content = content;
    return this;
  }

  public Response withMediaType(String mediaType) {
    this.mediaType = mediaType;
    return this;
  }

  public Response json(Object content) {
    this.content = content;
    this.mediaType = MediaType.APPLICATION_JSON;
    return this;
  }

  public Response addHeader(String key, String value) {
    if (StringValue.isNotEmpty(key)) {
      headers.add(key, value);
    }
    return this;
  }
}

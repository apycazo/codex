package es.asgarke.golem.http.types;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.asgarke.golem.tools.StringOps;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Singleton
public class JsonMediaTypeMapper implements MediaTypeMapper {

  private final ObjectMapper mapper;

  public JsonMediaTypeMapper() {
    this(new ObjectMapper());
  }

  public JsonMediaTypeMapper(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public boolean canMapMediaType(String mediaType) {
    if (StringOps.isEmpty(mediaType)) {
      return false;
    } else {
      String [] values = mediaType.split("\\s*;\\s*");
      return Arrays.stream(values)
        .map(String::toLowerCase)
        .filter(value -> value.startsWith("application/"))
        .map(value -> value.replace("application/", ""))
        .anyMatch(v -> v.contains("json"));
    }
  }

  @Override
  public byte[] toByteArray(Object object) throws IOException {
    byte[] bodyBytes = new byte[0];
    if (object != null) {
      bodyBytes = mapper.writeValueAsBytes(object);
    }
    return bodyBytes;
  }

  @Override
  public <T> T toObjectInstance(InputStream inputStream, Class<T> clazz) throws IOException {
    return mapper.readValue(inputStream, clazz);
  }

}

package es.asgarke.golem.http.types;

import es.asgarke.golem.http.definitions.MediaType;
import es.asgarke.golem.tools.StringTool;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Singleton
public class PlainTextMediaTypeMapper implements MediaTypeMapper {

  @Override
  public boolean canMapMediaType(String mediaType) {
    if (StringTool.isEmpty(mediaType)) {
      return false;
    } else {
      return mediaType.equalsIgnoreCase(MediaType.TEXT_PLAIN);
    }
  }

  @Override
  public byte[] toByteArray(Object object) {
    return object instanceof String ? ((String)object).getBytes() : new byte[0];
  }

  @Override
  public <T> T toObjectInstance(InputStream inputStream, Class<T> clazz) throws IOException {
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    String value = new BufferedReader(inputStreamReader).lines().collect(Collectors.joining("\n"));
    return clazz.cast(value);
  }
}

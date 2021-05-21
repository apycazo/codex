package es.asgarke.golem.http.types;

import es.asgarke.golem.tools.StringOps;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
@Singleton
public class PlainTextMediaTypeMapper implements MediaTypeMapper {

  @Override
  public boolean canMapMediaType(String mediaType) {
    if (StringOps.isEmpty(mediaType)) {
      return false;
    } else {
      return mediaType.toLowerCase().startsWith("text/");
    }
  }

  @Override
  public byte[] toByteArray(Object object) {
    if (object instanceof byte[]) {
      return (byte[])object;
    } else if (object instanceof String) {
      return ((String)object).getBytes();
    } else {
      return new byte[0];
    }
  }

  @Override
  public <T> T toObjectInstance(InputStream inputStream, Class<T> clazz) {
    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    String value = new BufferedReader(inputStreamReader).lines().collect(Collectors.joining("\n"));
    return clazz.cast(value);
  }
}

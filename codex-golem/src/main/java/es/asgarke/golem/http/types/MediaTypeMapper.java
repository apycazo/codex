package es.asgarke.golem.http.types;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines a class capable of serializing a given media type to a byte array.
 */
public interface MediaTypeMapper {

  boolean canMapMediaType(String mediaType);
  byte[] toByteArray(Object object) throws IOException;
  <T> T toObjectInstance(InputStream inputStream, Class<T> clazz) throws IOException;
}

package es.asgarke.golem.http.types;

import es.asgarke.golem.tools.StringOps;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class BinaryMediaTypeMapper implements MediaTypeMapper {

  @Override
  public boolean canMapMediaType(String mediaType) {
    if (StringOps.isEmpty(mediaType)) {
      return false;
    } else {
      String media = mediaType.toLowerCase();
      return media.startsWith("image/") || media.startsWith("audio/") || media.startsWith("video/");
    }
  }

  @Override
  public byte[] toByteArray(Object object) throws IOException {
    if (object instanceof byte[]) {
      return (byte[])object;
    } else {
      throw new IOException("Object must be a byte[]");
    }
  }

  @Override
  public <T> T toObjectInstance(InputStream inputStream, Class<T> clazz) throws IOException {
    throw new IOException("Object casting not available");
  }
}

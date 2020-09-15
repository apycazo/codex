package apycazo.codex.feign;

import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FeignCustomDecoder implements Decoder {
  private final JacksonDecoder jacksonDecoder;
  private final Decoder.Default defaultDecoder;
  private final Map<String, Decoder> decoderMap;

  public FeignCustomDecoder() {
    jacksonDecoder = new JacksonDecoder();
    defaultDecoder = new Decoder.Default();
    decoderMap = new HashMap<>();
    decoderMap.put("", defaultDecoder);
    decoderMap.put("application/json", jacksonDecoder);
  }

  public FeignCustomDecoder decodeMediaTypeWithJackson(String mediaType) {
    return decodeMediaTypeWithProvided(mediaType, jacksonDecoder);
  }

  public FeignCustomDecoder decodeMediaTypeWithDefault(String mediaType) {
    return decodeMediaTypeWithProvided(mediaType, defaultDecoder);
  }

  public FeignCustomDecoder decodeMediaTypeWithProvided(
    String mediaType, Decoder decoder) {
    if ((mediaType != null && !mediaType.isBlank()) && decoder != null) {
      decoderMap.put(mediaType, decoder);
    }
    return this;
  }

  @Override
  public Object decode(Response response, Type type)
    throws IOException, FeignException {
    Map<String, Collection<String>> headers = response.headers();
    String contentType = response.headers().keySet().stream()
      .filter(hdr -> hdr.equalsIgnoreCase("content-type"))
      .findFirst()
      .map(headers::get)
      .orElse(Collections.singletonList(""))
      .iterator().next().toLowerCase();
    return decoderMap
      .getOrDefault(contentType, defaultDecoder)
      .decode(response, type);
  }
}

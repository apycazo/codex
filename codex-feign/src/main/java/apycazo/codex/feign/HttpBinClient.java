package apycazo.codex.feign;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.Map;

public interface HttpBinClient {

  @RequestLine("GET /get?source={source}")
  Map<String, Object> get(@Param("source") String owner);

  @RequestLine("POST /post")
  @Headers({"content-type:application/json"})
  Map<String, Object> post(Map<String, Object> data);
}

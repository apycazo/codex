package apycazo.codex.feign;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class FeignDemo {

  public static void main(String[] args) {
    String url = "https://httpbin.org/";
    HttpBinClient httpBinClient = ClientFactory.create(HttpBinClient.class, url);
    Map<String, Object> response = httpBinClient.get("test");
    log.info("Response: {}", response);
    System.exit(0);
  }
}

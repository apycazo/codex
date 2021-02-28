package apycazo.codex.java.basic;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class JacksonUsage {

  @Test
  void deserialize_map() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    String value = "{'key1': 'value1', 'key2': 'value2'}";
    TypeReference<Map<String,Object>> ref = new TypeReference<>() {};
    Map<String, Object> map = mapper.readValue(value, ref);
    assertThat(map).isNotNull();
    assertThat(map.size()).isEqualTo(2);
    assertThat(map.get("key1")).isEqualTo("value1");
    assertThat(map.get("key2")).isEqualTo("value2");
  }
}

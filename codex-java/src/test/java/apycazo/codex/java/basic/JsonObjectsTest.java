package apycazo.codex.java.basic;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonObjectsTest {

  @Test
  void basicJsonViewTest() throws Exception {
    // by default, all properties not explicitly marked as being part of a view are serialized, disable that with:
    JsonMapper mapper = JsonMapper.builder()
      .disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
      .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
      .build();
    // test data
    String content = "{'name':'test', 'pwd':'secret', 'numberAsString':10,'privateId':'101'}";

    // deserialize
    JsonObjects instance = mapper.readValue(content, JsonObjects.class);
    assertThat(instance).isNotNull();
    assertThat(instance.getName()).isEqualTo("test");
    assertThat(instance.getPwd()).isEqualTo("secret");
    assertThat(instance.getNumberAsString()).isEqualTo("10");
    assertThat(instance.getPrivateId()).isEqualTo("101");

    // serialize
    String json = mapper.writeValueAsString(instance);
    assertThat(json.indexOf("\"name\":\"test\"")).isGreaterThan(-1);
    assertThat(json.indexOf("\"pw\"")).isEqualTo(-1);

    // test views, public only
    json = mapper.writerWithView(JsonObjects.PublicView.class).writeValueAsString(instance);
    assertThat(json.indexOf("privateId")).isEqualTo(-1);
    assertThat(json.indexOf("name")).isGreaterThan(-1);

    // test views, private too
    json = mapper.writerWithView(JsonObjects.PrivateView.class).writeValueAsString(instance);
    assertThat(json.indexOf("privateId")).isGreaterThan(-1);
    assertThat(json.indexOf("name")).isGreaterThan(-1);
  }

}
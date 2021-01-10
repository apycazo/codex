package apycazo.codex.java.basic;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonObjects {

  public static class PublicView {}
  public static class PrivateView extends PublicView {}

  @JsonView(PublicView.class)
  private String name;
  @JsonView(PublicView.class)
  private String pwd;
  @JsonView(PublicView.class)
  @JsonSerialize(using = StringSerializer.class)
  private String numberAsString;
  @JsonView(PrivateView.class)
  private String privateId;

  @JsonIgnore // do not serialize this field
  public String getPwd() {
    return pwd;
  }

  @JsonProperty // but do deserialize it!
  public void setPwd(String value) {
    this.pwd = value;
  }

  public static void main(String[] args) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
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

    // by default, all properties not explicitly marked as being part of a view are serialized, disable that with:
    mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

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

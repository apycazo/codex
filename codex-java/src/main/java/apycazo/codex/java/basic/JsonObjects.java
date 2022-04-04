package apycazo.codex.java.basic;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}

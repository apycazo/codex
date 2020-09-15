package apycazo.codex.feign;

import feign.ExceptionPropagationPolicy;
import feign.Feign;
import feign.auth.BasicAuthRequestInterceptor;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;

public class ClientFactory {

  public static <T> T create(Class<? extends T> api, String serviceURL) {
    return create(api, serviceURL, null, null, null);
  }

  public static <T> T create(Class<? extends T> api, String serviceURL, UserPwd userPwd) {
    return create(api, serviceURL, null, null, userPwd);
  }

  public static <T> T create(Class<? extends T> api, String serviceURL,
    Encoder encoder, Decoder decoder, UserPwd userPwd) {
    if (encoder == null) encoder = new FormEncoder(new JacksonEncoder());
    if (decoder == null) decoder = new FeignCustomDecoder();

    Feign.Builder client = Feign.builder()
      .encoder(encoder)
      .decoder(decoder)
      .exceptionPropagationPolicy(ExceptionPropagationPolicy.UNWRAP)
      .logger(new Slf4jLogger())
      .client(new OkHttpClient());

    if (userPwd != null) {
      String usr = userPwd.getUser();
      String pwd = userPwd.getPwd();
      client.requestInterceptor(new BasicAuthRequestInterceptor(usr, pwd));
    }

    return client.target(api, serviceURL);
  }
}

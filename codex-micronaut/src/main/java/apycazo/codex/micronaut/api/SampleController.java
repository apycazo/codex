package apycazo.codex.micronaut.api;

import apycazo.codex.micronaut.config.About;
import apycazo.codex.micronaut.data.Outcome;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.reactivex.Single;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Controller("api")
public class SampleController {

  private final Logger log = LoggerFactory.getLogger(SampleController.class);

  @Inject
  private SampleService sampleService;
  @Inject
  private About about;

  @Get(uri = "/time", produces = MediaType.APPLICATION_JSON)
  public Single<Outcome<Void>> time() {
    log.info("Processing request for time");
    return Single.just(Outcome.<Void>builder().build());
  }

  @Get(uri = "/echo/{text}", produces = MediaType.APPLICATION_JSON)
  public Single<Outcome<String>> echo(String text) {
    return Single.just(sampleService.echo(text));
  }

  @Get(uri = "/about", produces = MediaType.APPLICATION_JSON)
  public Single<About> about() {
    return Single.just(about);
  }

  @Get(uri = "/echo", produces = MediaType.APPLICATION_JSON)
  public Single<Outcome<String>> echoParam(@QueryValue String text) {
    return Single.just(sampleService.echo(text));
  }

}

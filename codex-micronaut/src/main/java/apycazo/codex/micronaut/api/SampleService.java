package apycazo.codex.micronaut.api;

import apycazo.codex.micronaut.data.Outcome;

import javax.inject.Singleton;

@Singleton
public class SampleService {

  public Outcome<String> echo(String value) {
    return Outcome.<String>builder().payload(value).build();
  }
}

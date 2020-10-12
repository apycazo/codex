package es.asgarke.golem.http.dummy;

import es.asgarke.golem.core.annotations.Configuration;

import javax.inject.Singleton;

@Singleton
@Configuration(scanPackages = MathService.class)
public class MathService {

  public int sum(int a, int b) {
    return a + b;
  }
}

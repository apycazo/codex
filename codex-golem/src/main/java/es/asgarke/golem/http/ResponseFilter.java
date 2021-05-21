package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpExchange;
import es.asgarke.golem.common.Ordered;
import es.asgarke.golem.http.types.Response;

public interface ResponseFilter extends Ordered {

  /**
   * Filters the response, adding values or modifying the result. When more than one filter applies, all of them
   * will be applied to the response of the previous one.
   * @param currentResponse the response as it was returned by the rest resource, or the preceding filter.
   * @param exchange the current request exchange.
   * @return the final response to use. When null, the currentResponse value will be used as it is.
   */
  Response filterResponse(Response currentResponse, HttpExchange exchange);

  @Override
  default Integer getOrder() {
    return 5000;
  }
}

package es.asgarke.golem.http;

import com.sun.net.httpserver.HttpExchange;
import es.asgarke.golem.common.Ordered;
import es.asgarke.golem.http.types.Response;

public interface RequestFilter extends Ordered {

  /**
   * Filters the request, and return an intercepting response if the request must not continue. When multiple request
   * filters apply, if any of them returns a response, the chain will be halted.
   * @param exchange the current request exchange.
   * @return null to continue processing, any other response will be returned without calling the mapped resource.
   */
  Response filterRequest(HttpExchange exchange);

  @Override
  default Integer getOrder() {
    return 5000;
  }
}

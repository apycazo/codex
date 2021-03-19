package apycazo.codex.rest.common.filter;

import apycazo.codex.rest.common.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;
import java.util.Optional;

@Slf4j
public class RequestFilter {

  public static final String HEADER_REQUEST_ID = "x-request-id";
  public static final String MDC_REQUEST_ID_KEY = HEADER_REQUEST_ID;
  public static final String MDC_OPERATION_KEY = "x-operation";
  public static final String SYSTEM = "system";

  protected void accept(HttpServletRequest request) {
    accept(request, null,null);
  }

  protected void accept(HttpServletRequest request, RequestExtraData extraData, ResourceInfo resourceInfo) {
    // -- maintain the request id, if received as a frame-id
    String frameId = request.getHeader(HEADER_REQUEST_ID);
    if (frameId == null) {
      frameId = Optional.ofNullable(extraData)
        .map(RequestExtraData::getRequestId)
        .orElse(IdGenerator.generateId());
    } else if (extraData != null) {
      extraData.setRequestId(frameId);
    }
    MDC.put(MDC_REQUEST_ID_KEY, frameId);
    // -- set the operation ID
    String operationName = Optional.ofNullable(resourceInfo)
      .map(v -> v.getResourceClass().getName() + "::" + v.getResourceMethod().getName())
      .orElse(SYSTEM);
    MDC.put(MDC_OPERATION_KEY, operationName);
  }
}

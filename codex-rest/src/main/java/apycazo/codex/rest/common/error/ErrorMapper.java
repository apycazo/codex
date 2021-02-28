package apycazo.codex.rest.common.error;

import apycazo.codex.rest.common.util.FromContext;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ErrorMapper implements ExceptionMapper<Exception> {

  @Override
  public Response toResponse(Exception exception) {
    if (exception instanceof ServiceException) {
      ServiceException serviceException = (ServiceException) exception;
      return Response
        .status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(serviceException.getErrorInfo())
        .type(MediaType.APPLICATION_JSON_TYPE)
        .build();
    } else {
      ErrorInfo errorInfo = ErrorInfo.builder()
        .message(exception.getMessage())
        .errorCode(ErrorCode.GenericError)
        .requestId(FromContext.takeRequestId()).build();
      return Response
        .status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(errorInfo)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .build();
    }
  }
}

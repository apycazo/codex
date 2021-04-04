package apycazo.codex.auth.saml2;

import apycazo.codex.auth.common.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.cert.CertificateEncodingException;

/**
 * Testing utils: https://samltest.id/
 * Also check: https://medium.com/the-new-control-plane/i-need-a-saml-idp-to-test-now-477761595b60
 */
@Slf4j
@Path("saml")
public class Saml2Endpoint {

  private final Saml2Service service;
  private final URI welcome;

  @Autowired
  public Saml2Endpoint(Saml2Service service) throws URISyntaxException {
    this.service = service;
    this.welcome = new URI("/welcome");
  }

  @GET
  @Produces(MediaType.APPLICATION_XML)
  public Response getMetadata() throws CertificateEncodingException {
    return Response.ok(service.getSPMetadata()).build();
  }

  @GET
  @Path("login")
  public Response doLoginRequest(@Context HttpSession session,
    @Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
    log.info("Received SAML login request");
    SessionUtil.startSso(session);
    return service.redirectBinding("", request, response);
  }

  @POST
  @Path("login")
  public Response processLoginResponse(@Context HttpSession session,
    @Context HttpServletRequest request, @Context HttpServletResponse response) {
    log.info("Received SAML login response");
    boolean isAuthenticated = service.processLoginResponse(request, response);
    if (isAuthenticated) {
      SessionUtil.completeSso(session);
      return Response.temporaryRedirect(welcome).build();
    } else {
      SessionUtil.failSso(session);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @POST
  @Path("logout")
  public Response logout(@Context HttpSession session) {
    log.info("Received SAML logout request");
    SessionUtil.clearAuthInfo(session);
    return Response.ok().build();
  }
}

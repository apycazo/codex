package apycazo.codex.jersey.api;

import apycazo.codex.jersey.InfoResource;
import apycazo.codex.jersey.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class RootEndpoint {

  @Autowired
  private DemoService demoService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public InfoResource timestamp() {
    return demoService.getInfo();
  }
}

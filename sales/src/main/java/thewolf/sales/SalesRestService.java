package thewolf.sales;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Named
@Path("/salesorders")
@Produces({MediaType.APPLICATION_JSON})
public class SalesRestService {

  @Inject
  private SalesService salesService;

  @GET
  @Path("{id}")
  public SalesOrder get(@PathParam("id") int id) {
    return salesService.getOrder(id);
  }

}

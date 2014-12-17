package thewolf.sales;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

@Named
@Path("/salesorders")
@Produces({MediaType.APPLICATION_JSON})
public class SalesRestService {

  @Inject
  private SalesService salesService;
  @Context
  private UriInfo uriInfo;

  @GET
  @Path("/{id}")
  public SalesOrder get(@PathParam("id") int id) {
    return salesService.getOrder(id);
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  public Response post(SalesOrder salesOrder) {
    SalesOrder addedOrder = salesService.addOrder(salesOrder);
    UriBuilder fromResource = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "get");
    URI location = fromResource.build(addedOrder.getId());
    return Response.created(location).build();
  }
}

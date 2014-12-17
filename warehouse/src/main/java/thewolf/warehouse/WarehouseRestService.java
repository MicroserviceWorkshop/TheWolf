package thewolf.warehouse;

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
@Path("/warehouse")
@Produces({ MediaType.APPLICATION_JSON })
public class WarehouseRestService {

	@Inject
	private WarehouseService warehouseService;

	@Context
	private UriInfo uriInfo;

	@GET
	@Path("/{id}")
	public WarehouseEntry get(@PathParam("id") int id) {
		return warehouseService.getWarehouseEntry(id);
	}

	@POST
	@Consumes(value = { MediaType.APPLICATION_JSON })
	public Response post(WarehouseEntry entry) {

		warehouseService.addProducts(entry.getProductId(), entry.getAmount());

		UriBuilder fromResource = uriInfo.getBaseUriBuilder().path(getClass())
				.path(getClass(), "get");
		URI location = fromResource.build(entry.getProductId());
		return Response.created(location).build();
	}
}

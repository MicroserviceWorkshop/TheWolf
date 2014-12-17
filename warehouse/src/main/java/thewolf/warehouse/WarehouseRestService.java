package thewolf.warehouse;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Named
@Path("/warehouse")
@Produces({MediaType.APPLICATION_JSON})
public class WarehouseRestService {
	
	@Inject
	private WarehouseService warehouseService;

	@GET
    @Path("/{id}")
    public WarehouseEntry get(@PathParam("id") int id) {
        return warehouseService.getWarehouseEntry(id);
    }
	
}

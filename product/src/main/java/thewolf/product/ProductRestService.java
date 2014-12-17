package thewolf.product;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Named
@Path("/products")
@Produces({MediaType.APPLICATION_JSON})
public class ProductRestService {
	
	@Inject
	private ProductService productService;

	@GET
    @Path("/{id}")
    public Product get(@PathParam("id") int id) {
        return productService.getProduct(id);
    }
	
}

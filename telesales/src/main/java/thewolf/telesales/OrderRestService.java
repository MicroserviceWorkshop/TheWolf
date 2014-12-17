package thewolf.telesales;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Named
@Path("/orders")
@Produces({ MediaType.APPLICATION_JSON })
public class OrderRestService {

	@Inject
	private OrderService orderService;
	
	@Context 
	private UriInfo uriInfo;

	@POST
	public Response createOrder(Order order) {
		orderService.createOrder(order);
		URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "getOrder").build(order.getId());
		return Response.created(location).build();
	}
	
	@GET
	public Response getOrders() {
		List<Order> orders = orderService.getOrders();
		return Response.ok(orders).build();
	}
	
	@GET
	@Path("{id}")
	public Response getOrder(@PathParam("id") int id) {
		Order order = orderService.getOrder(id);
		return Response.ok(order).build();
	}
	
	@PUT
	@Path("{id}/release")
	public Response releaseOrder(@PathParam("id") int id) {
		Order order = orderService.getOrder(id);
		orderService.releaseOrder(order);
		return Response.ok().build();
	}

}

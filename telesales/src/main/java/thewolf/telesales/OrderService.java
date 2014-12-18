package thewolf.telesales;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.transaction.Transactional;
import javax.ws.rs.core.UriBuilder;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.springframework.web.client.RestTemplate;

@Named
@Transactional
public class OrderService {

	private int currentOrderId = 1;
	private static Map<Integer, Order> orders = new HashMap<Integer, Order>();

	static {
		Order order = new Order();
		order.setId(0);
		order.setProductId(1);
		order.setQuantity(10);
		orders.put(0, order);
	}

	public Order createOrder(Order order) {
		Integer id = currentOrderId++;
		order.setId(currentOrderId);
		orders.put(id, order);
		return order;
	}

	public List<Order> getOrders() {
		return new ArrayList<Order>(orders.values());
	}

	public Order getOrder(int id) {
		return orders.get(id);
	}

	public void releaseOrder(Order order) throws Exception {
		SalesOrder salesOrder = new SalesOrder();
		salesOrder.setCustomerName("Hans Black");

		CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(
				"localhost:2181", new RetryNTimes(5, 1000));
		curatorFramework.start();

		ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder
				.builder(Void.class).basePath("thewolf")
				.client(curatorFramework).build();
		serviceDiscovery.start();

		ServiceProvider<Void> serviceProvider = serviceDiscovery
				.serviceProviderBuilder().serviceName("sales").build();
		serviceProvider.start();

		ServiceInstance<Void> serviceInstance = serviceProvider.getInstance();
		String salesServiceAddress = UriBuilder.fromPath(serviceInstance.buildUriSpec()).path("salesorders").toString();
		System.out.println("Sales Service Address: ".concat(salesServiceAddress));

		RestTemplate restTemplate = new RestTemplate();
		URI uri = restTemplate.postForLocation(salesServiceAddress, salesOrder);
		System.out.println(uri.toASCIIString());
	}

}

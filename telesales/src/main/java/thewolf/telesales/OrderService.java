package thewolf.telesales;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.transaction.Transactional;

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

	public void releaseOrder(Order order) {
		orders.remove(order.getId());
	}


}

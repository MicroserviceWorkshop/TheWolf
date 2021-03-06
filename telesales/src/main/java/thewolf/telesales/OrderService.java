package thewolf.telesales;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class OrderService {

  private int currentOrderId = 1;
  private static Map<Integer, Order> orders = new ConcurrentHashMap<>();

  @Inject
  private ServiceLocator serviceLocator;

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
    // SalesOrder salesOrder = new SalesOrder();
    // salesOrder.setCustomerName("Hans Black");
    //
    // URI serviceUri = serviceLocator.locate("sales", "salesorders");
    //
    // RestTemplate restTemplate = new RestTemplate();
    // URI uri = restTemplate.postForLocation(serviceUri, salesOrder);
    // System.out.printf("Called service %s and got result %s\n", serviceUri.toString(),
    // uri.toString());

    URI serviceUri = serviceLocator.locate("sales", "salesorders");
    new OrderServiceReleaseCommand(serviceUri).execute();
  }
}

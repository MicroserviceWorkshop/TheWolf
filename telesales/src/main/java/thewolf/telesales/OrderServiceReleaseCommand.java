package thewolf.telesales;

import java.net.URI;

import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class OrderServiceReleaseCommand extends HystrixCommand<Void> {

  private final URI serviceUri;

  public OrderServiceReleaseCommand(URI uri) {
    super(HystrixCommandGroupKey.Factory.asKey(uri.toString()));
    this.serviceUri = uri;
  }

  @Override
  protected Void run() {
    SalesOrder salesOrder = new SalesOrder();
    salesOrder.setCustomerName("Hans Black");

    RestTemplate restTemplate = new RestTemplate();
    URI uri = restTemplate.postForLocation(serviceUri, salesOrder);
    System.out.printf("Called service %s and got result %s\n", uri.toString(), uri.toString());

    return null;
  }

  @Override
  protected Void getFallback() {
    System.out.println("HYSTRIX FALLBACK");
    return null;
  }


}

package thewolf.sales;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Transactional
public class SalesService {

  public SalesOrder getOrder(int id) {
    SalesOrder salesOrder = new SalesOrder();
    salesOrder.setId(id);
    salesOrder.setCustomerName("Marco");
    return salesOrder;
  }

  public SalesOrder addOrder(SalesOrder salesOrder) {
    salesOrder.setId((int) (Math.random() * 5000));
    return salesOrder;
  }

}

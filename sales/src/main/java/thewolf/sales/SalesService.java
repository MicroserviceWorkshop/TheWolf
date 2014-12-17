package thewolf.sales;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Named
@Transactional
public class SalesService {

  @PersistenceContext
  private EntityManager em;

  public SalesOrder getOrder(int id) {
    return em.find(SalesOrder.class, id);
  }

  public SalesOrder addOrder(SalesOrder salesOrder) {
    em.persist(salesOrder);
    return salesOrder;
  }

}

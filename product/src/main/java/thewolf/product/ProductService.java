package thewolf.product;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Transactional
public class ProductService {

	public Product getProduct(int id) {
		Product product = new Product();
		product.setId(id);
		product.setName("My Product " + id);
		return product;
	}

}

package thewolf.warehouse;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Transactional
public class WarehouseService {

	private Map<Integer, Integer> entries = new HashMap<>();
	
	public WarehouseEntry getWarehouseEntry(int productId) {
		WarehouseEntry warehouseEntry = new WarehouseEntry();
		warehouseEntry.setProductId(productId);
		
		int amount = entries.getOrDefault(productId, 0);
		
		warehouseEntry.setAmount(amount);
		return warehouseEntry;
	}

	public void addProducts(int productId, int amount){
		int currentAmount = entries.getOrDefault(productId, 0);

		entries.put(productId, currentAmount + amount);
	}
}

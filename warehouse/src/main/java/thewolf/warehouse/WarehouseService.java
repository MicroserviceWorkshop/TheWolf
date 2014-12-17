package thewolf.warehouse;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Transactional
public class WarehouseService {

	public WarehouseEntry getWarehouseEntry(int id) {
		WarehouseEntry warehouseEntry = new WarehouseEntry();
		warehouseEntry.setProductId(id);
		warehouseEntry.setAmount(123);
		return warehouseEntry;
	}

}

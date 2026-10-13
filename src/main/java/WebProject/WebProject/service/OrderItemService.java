package WebProject.WebProject.service;

import java.util.List;

import WebProject.WebProject.entity.OrderItem;

public interface OrderItemService {

	List<OrderItem> getAllByOrder_Id(Long id);

	public void saveOrder_Item(OrderItem order_Item);

	void deleteById(Long id);
}

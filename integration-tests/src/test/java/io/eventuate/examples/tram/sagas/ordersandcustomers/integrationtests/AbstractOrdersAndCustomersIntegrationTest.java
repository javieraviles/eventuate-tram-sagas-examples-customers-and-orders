package io.eventuate.examples.tram.sagas.ordersandcustomers.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain.Customer;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.service.CustomerService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.OrderService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.domain.Product;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.service.ProductService;

public abstract class AbstractOrdersAndCustomersIntegrationTest {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ProductService productService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Test
	public void shouldApproveOrder() throws InterruptedException {
		Money creditLimit = new Money("15.00");
		Customer customer = customerService.createCustomer("Fred", creditLimit);
		int stockLimit = 12;
		Product product = productService.createProduct("Chair", stockLimit);
		Order order = orderService
				.createOrder(new OrderDetails(customer.getId(), product.getId(), 6, new Money("12.34")));

		assertOrderState(order.getId(), OrderState.APPROVED);
	}

	@Test
	public void shouldRejectOrderDueToCreditLimit() throws InterruptedException {
		Money creditLimit = new Money("15.00");
		Customer customer = customerService.createCustomer("Fred", creditLimit);
		int stockLimit = 12;
		Product product = productService.createProduct("Chair", stockLimit);
		Order order = orderService.createOrder(new OrderDetails(customer.getId(), product.getId(), 6, new Money("123.40")));
	
		assertOrderState(order.getId(), OrderState.REJECTED);
	  }

	@Test
	public void shouldRejectOrderDueToStockLimit() throws InterruptedException {
		Money creditLimit = new Money("123.40");
		Customer customer = customerService.createCustomer("Fred", creditLimit);
		int stockLimit = 6;
		Product product = productService.createProduct("Chair", stockLimit);
		Order order = orderService.createOrder(new OrderDetails(customer.getId(), product.getId(), 12, new Money("15.00")));

		assertOrderState(order.getId(), OrderState.REJECTED);
	}

	private void assertOrderState(Long id, OrderState expectedState) throws InterruptedException {
		Order order = null;
		for (int i = 0; i < 30; i++) {
			order = transactionTemplate.execute(s -> orderRepository.findById(id)).orElseThrow(
					() -> new IllegalArgumentException(String.format("Order with id %s is not found", id)));
			if (order.getState() == expectedState)
				break;
			TimeUnit.MILLISECONDS.sleep(400);
		}

		assertEquals(expectedState, order.getState());
	}
}

package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common.OrderDetails;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.Order;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderRepository;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.service.OrderService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;

@RestController
public class OrderController {

	private OrderService orderService;
	private OrderRepository orderRepository;

	@Autowired
	public OrderController(OrderService orderService, OrderRepository orderRepository) {
		this.orderService = orderService;
		this.orderRepository = orderRepository;
	}

	@RequestMapping(value = "/orders", method = RequestMethod.POST)
	public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
		Order order = orderService
				.createOrder(new OrderDetails(createOrderRequest.getCustomerId(), createOrderRequest.getProductId(),
						createOrderRequest.getOrderUnits(), createOrderRequest.getOrderTotal()));
		return new CreateOrderResponse(order.getId());
	}

	@RequestMapping(value = "/orders/{orderId}", method = RequestMethod.GET)
	public ResponseEntity<GetOrderResponse> getOrder(@PathVariable Long orderId) {
		return orderRepository.findById(orderId)
				.map(o -> new ResponseEntity<>(new GetOrderResponse(o.getId(), o.getState(), o.getRejectionReason()),
						HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}

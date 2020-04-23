package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;

public class CreateOrderRequest {
	private Money orderTotal;
	private Long customerId;
	private Long productId;
	private int orderUnits;

	public CreateOrderRequest() {
	}

	public CreateOrderRequest(Long customerId, Long productId, int orderUnits, Money orderTotal) {
		super();
		this.orderTotal = orderTotal;
		this.customerId = customerId;
		this.productId = productId;
		this.orderUnits = orderUnits;
	}

	public Money getOrderTotal() {
		return orderTotal;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public Long getProductId() {
		return productId;
	}

	public int getOrderUnits() {
		return orderUnits;
	}

}

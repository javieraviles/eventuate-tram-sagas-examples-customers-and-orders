package io.eventuate.examples.tram.sagas.ordersandcustomers.orders.common;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;

@Embeddable
public class OrderDetails {

	private Long customerId;
	private Long productId;
	private int orderUnits;

	@Embedded
	private Money orderTotal;

	public OrderDetails() {
	}

	public OrderDetails(Long customerId, Long productId, int orderUnits, Money orderTotal) {
		this.customerId = customerId;
		this.orderTotal = orderTotal;
		this.productId = productId;
		this.orderUnits = orderUnits;
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

	public Money getOrderTotal() {
		return orderTotal;
	}
}

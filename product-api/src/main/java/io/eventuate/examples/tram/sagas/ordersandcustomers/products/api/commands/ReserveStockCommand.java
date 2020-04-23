package io.eventuate.examples.tram.sagas.ordersandcustomers.products.api.commands;

import io.eventuate.tram.commands.common.Command;

public class ReserveStockCommand implements Command {
	private Long orderId;
	private int orderUnits;
	private long productId;

	public ReserveStockCommand() {
	}

	public ReserveStockCommand(Long productId, Long orderId, int orderUnits) {
		this.productId = productId;
		this.orderId = orderId;
		this.orderUnits = orderUnits;
	}

	public int getOrderUnits() {
		return orderUnits;
	}

	public void setOrderUnits(int orderUnits) {
		this.orderUnits = orderUnits;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {

		this.orderId = orderId;
	}

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}
}

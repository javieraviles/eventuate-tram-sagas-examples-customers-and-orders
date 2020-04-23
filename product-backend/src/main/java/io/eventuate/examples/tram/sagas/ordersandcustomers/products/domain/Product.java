package io.eventuate.examples.tram.sagas.ordersandcustomers.products.domain;

import java.util.Collections;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Product")
@Access(AccessType.FIELD)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private int stock;

	@ElementCollection
	private Map<Long, Integer> stockReservations;

	int availableStock() {
		return stock - stockReservations.values().stream().reduce(0, (subtotal, element) -> subtotal + element);
	}

	public Product() {
	}

	public Product(String name, int stock) {
		this.name = name;
		this.stock = stock;
		this.stockReservations = Collections.emptyMap();
	}

	public Long getId() {
		return id;
	}

	public void reserveStock(Long orderId, int orderUnits) {
		if (availableStock() > orderUnits) {
			stockReservations.put(orderId, orderUnits);
		} else
			throw new ProductStockLimitExceededException();
	}
}

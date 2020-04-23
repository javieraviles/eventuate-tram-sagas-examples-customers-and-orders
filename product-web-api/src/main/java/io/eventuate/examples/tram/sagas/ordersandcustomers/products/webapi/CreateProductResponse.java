package io.eventuate.examples.tram.sagas.ordersandcustomers.products.webapi;

public class CreateProductResponse {
	private Long productId;

	public CreateProductResponse() {
	}

	public CreateProductResponse(Long productId) {
		this.productId = productId;
	}

	public Long getproductId() {
		return productId;
	}

	public void setproductId(Long productId) {
		this.productId = productId;
	}
}

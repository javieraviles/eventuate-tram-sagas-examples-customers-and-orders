package io.eventuate.examples.tram.sagas.ordersandcustomers.products.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.eventuate.examples.tram.sagas.ordersandcustomers.products.domain.Product;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.service.ProductService;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.webapi.CreateProductRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.webapi.CreateProductResponse;

@RestController
public class ProductController {

	private ProductService productService;

	@Autowired
	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@RequestMapping(value = "/products", method = RequestMethod.POST)
	public CreateProductResponse createProduct(@RequestBody CreateProductRequest createProductRequest) {
		Product product = productService.createProduct(createProductRequest.getName(), createProductRequest.getStock());
		return new CreateProductResponse(product.getId());
	}
}

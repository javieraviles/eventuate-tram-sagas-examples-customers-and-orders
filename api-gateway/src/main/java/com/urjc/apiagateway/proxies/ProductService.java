package com.urjc.apiagateway.proxies;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class ProductService {
	public Mono<ProductInfo> findProductById(String productId) {
		return Mono.error(new UnsupportedOperationException());
	}
}

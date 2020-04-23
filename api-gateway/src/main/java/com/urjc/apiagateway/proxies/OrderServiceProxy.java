package com.urjc.apiagateway.proxies;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;
import reactor.core.publisher.Mono;

@Service
public class OrderServiceProxy {

	private WebClient client;

	public OrderServiceProxy(WebClient client) {
		this.client = client;
	}

	public Mono<GetOrderResponse> findOrderById(String orderId) {
		Mono<ClientResponse> response = client.get().uri("http://orderservice:8080/orders/{orderId}", orderId)
				.exchange();
		return response.flatMap(resp -> {
			switch (resp.statusCode()) {
			case OK:
				return resp.bodyToMono(OrderInfo.class);
			case NOT_FOUND:
				return Mono.error(new OrderNotFoundException());
			default:
				return Mono.error(new RuntimeException("Unknown" + resp.statusCode()));
			}
		});
	}

}

package com.urjc.apiagateway.orders;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.urjc.apiagateway.proxies.OrderServiceProxy;
import com.urjc.apiagateway.proxies.ProductService;

public class OrderConfiguration {

	@Bean
	public RouterFunction<ServerResponse> orderHandlerRouting(OrderHandlers orderHandlers) {
		return RouterFunctions.route(GET("/orders/{orderId}"), orderHandlers::getOrderDetails);
	}

	@Bean
	public OrderHandlers orderHandlers(OrderServiceProxy orderService, ProductService productService) {
		return new OrderHandlers(orderService, productService);
	}

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}

}

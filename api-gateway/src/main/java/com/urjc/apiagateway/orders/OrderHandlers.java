package com.urjc.apiagateway.orders;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import java.awt.PageAttributes.MediaType;

import org.omg.CORBA.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.urjc.apiagateway.proxies.OrderNotFoundException;
import com.urjc.apiagateway.proxies.OrderServiceProxy;
import com.urjc.apiagateway.proxies.ProductService;

import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;

import reactor.core.publisher.Mono;
import reactor.util.function.Tuple4;

public class OrderHandlers {

	private OrderServiceProxy orderService;
	private ProductService productService;

	public OrderHandlers(OrderServiceProxy orderService, ProductService productService) {
		this.orderService = orderService;
		this.productService = productService;
	}

	public Mono<ServerResponse> getOrderDetails(ServerRequest serverRequest) {
		String orderId = serverRequest.pathVariable("orderId");

		Mono<GetOrderResponse> orderInfo = Mono.first(orderService.findOrderById(orderId));
		Mono<ProductInfo> productInfo = orderInfo.flatMap(o -> productService.findProductById(o.getProductId()));

		Mono<Tuple4<GetOrderResponse, ProductInfo>> combined = Mono.zip(orderInfo, productInfo);

		Mono<OrderDetails> orderDetails = combined.map(OrderDetails::makeOrderDetails);

		return orderDetails
				.flatMap(od -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(od)))
				.onErrorResume(OrderNotFoundException.class, e -> ServerResponse.notFound().build());
	}

}

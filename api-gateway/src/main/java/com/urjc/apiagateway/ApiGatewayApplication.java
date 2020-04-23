package com.urjc.apiagateway;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ComponentScan
public class ApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(ApiGatewayApplication.class, args);
  }
  
  @Bean
	public RouteLocator proxyRouting(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(r -> r.path("/orders/**")
						.filters(f -> f.rewritePath("/orders/(?<RID>.*)", "/orders/${RID}"))
						.uri("http://orderservice:8080"))
				.route(r -> r.path("/customers/")
						.uri("http://customerservice:8080"))
				.route(r -> r.path("/products/")
						.uri("http://productservice:8080")).build();
	}
}


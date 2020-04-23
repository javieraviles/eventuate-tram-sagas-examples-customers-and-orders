package io.eventuate.examples.tram.sagas.ordersandcustomers.endtoendtests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.CreateCustomerRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.customers.webapi.CreateCustomerResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.OrderState;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.domain.RejectionReason;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.CreateOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.orders.webapi.GetOrderResponse;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.webapi.CreateProductRequest;
import io.eventuate.examples.tram.sagas.ordersandcustomers.products.webapi.CreateProductResponse;
import io.eventuate.util.test.async.Eventually;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = CustomersAndOrdersE2ETestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomersAndOrdersE2ETest {

	@Value("#{systemEnvironment['DOCKER_HOST_IP']}")
	private String hostName;

	private String baseUrlOrders(String path) {
		return "http://" + hostName + ":8081/" + path;
	}

	private String baseUrlCustomers(String path) {
		return "http://" + hostName + ":8082/" + path;
	}

	private String baseUrlProducts(String path) {
		return "http://" + hostName + ":8083/" + path;
	}

	@Autowired
	RestTemplate restTemplate;

	@Test
	public void shouldApprove() {
		CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrlCustomers("customers"),
				new CreateCustomerRequest("Fred", new Money("15.00")), CreateCustomerResponse.class);

		CreateProductResponse createProductResponse = restTemplate.postForObject(baseUrlProducts("products"),
				new CreateProductRequest("Chair", 12), CreateProductResponse.class);

		CreateOrderResponse createOrderResponse = restTemplate
				.postForObject(baseUrlOrders("orders"),
						new CreateOrderRequest(createCustomerResponse.getCustomerId(),
								createProductResponse.getproductId(), 6, new Money("12.34")),
						CreateOrderResponse.class);

		assertOrderState(createOrderResponse.getOrderId(), OrderState.APPROVED, null);
	}

	@Test
	public void shouldRejectBecauseOfInsufficientCredit() {
		CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrlCustomers("customers"),
				new CreateCustomerRequest("Fred", new Money("15.00")), CreateCustomerResponse.class);

		CreateProductResponse createProductResponse = restTemplate.postForObject(baseUrlProducts("products"),
				new CreateProductRequest("Chair", 12), CreateProductResponse.class);

		CreateOrderResponse createOrderResponse = restTemplate
				.postForObject(baseUrlOrders("orders"),
						new CreateOrderRequest(createCustomerResponse.getCustomerId(),
								createProductResponse.getproductId(), 6, new Money("123.40")),
						CreateOrderResponse.class);

		assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.INSUFFICIENT_CREDIT);
	}

	@Test
	public void shouldRejectBecauseOfUnknownCustomer() {

		CreateProductResponse createProductResponse = restTemplate.postForObject(baseUrlProducts("products"),
				new CreateProductRequest("Chair", 12), CreateProductResponse.class);

		CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrlOrders("orders"),
				new CreateOrderRequest(Long.MAX_VALUE, createProductResponse.getproductId(), 6, new Money("123.40")),
				CreateOrderResponse.class);

		assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.UNKNOWN_CUSTOMER);
	}

	@Test
	public void shouldRejectBecauseOfInsufficientStock() {
		CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrlCustomers("customers"),
				new CreateCustomerRequest("Fred", new Money("15.00")), CreateCustomerResponse.class);

		CreateProductResponse createProductResponse = restTemplate.postForObject(baseUrlProducts("products"),
				new CreateProductRequest("Chair", 6), CreateProductResponse.class);

		CreateOrderResponse createOrderResponse = restTemplate
				.postForObject(baseUrlOrders("orders"),
						new CreateOrderRequest(createCustomerResponse.getCustomerId(),
								createProductResponse.getproductId(), 12, new Money("12.34")),
						CreateOrderResponse.class);

		assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.INSUFFICIENT_CREDIT);
	}

	@Test
	public void shouldRejectBecauseOfUnknownProduct() {

		CreateCustomerResponse createCustomerResponse = restTemplate.postForObject(baseUrlCustomers("customers"),
				new CreateCustomerRequest("Fred", new Money("15.00")), CreateCustomerResponse.class);

		CreateOrderResponse createOrderResponse = restTemplate.postForObject(baseUrlOrders("orders"),
				new CreateOrderRequest(createCustomerResponse.getCustomerId(), Long.MAX_VALUE, 6, new Money("123.40")),
				CreateOrderResponse.class);

		assertOrderState(createOrderResponse.getOrderId(), OrderState.REJECTED, RejectionReason.UNKNOWN_CUSTOMER);
	}

	private void assertOrderState(Long id, OrderState expectedState, RejectionReason expectedRejectionReason) {
		Eventually.eventually(() -> {
			ResponseEntity<GetOrderResponse> getOrderResponseEntity = restTemplate
					.getForEntity(baseUrlOrders("orders/" + id), GetOrderResponse.class);
			assertEquals(HttpStatus.OK, getOrderResponseEntity.getStatusCode());
			GetOrderResponse order = getOrderResponseEntity.getBody();
			assertEquals(expectedState, order.getOrderState());
			assertEquals(expectedRejectionReason, order.getRejectionReason());
		});

	}

}

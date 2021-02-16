package com.sigea.publicService.web.rest;

import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import com.sigea.publicService.domain.Subscription;
import com.sigea.publicService.domain.enums.Gender;
import com.sigea.publicService.repository.SubscriptionRepository;
import com.sigea.publicService.service.impl.SubscriptionPublicServiceImpl;
import com.sigea.publicService.web.dto.SubscriptionDTO;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = SubscriptionPublicController.class)
@Import(SubscriptionPublicServiceImpl.class)
@Log4j2
public class SubscriptionPublicControllerTests {
	
	public static final String SUBSCRIPTION_ID = "subscriptionID";

	@MockBean
    SubscriptionRepository subscriptionRepository;
	
	@Autowired
	private WebTestClient webClient;
	
	@Test
	public void testGetAllSubscriptionAnyInSystem() {
		webClient.get().uri("/subscription")
        	.header(HttpHeaders.ACCEPT, "application/json")
        	.exchange()
        	.expectStatus().isOk()
        	.expectBodyList(SubscriptionDTO.class);
	}
	
	
	@Test
	public void testGetAllSubscription() {
		List<Subscription> subscriptionList = new ArrayList<Subscription>();
		subscriptionList.add(generateSubscription());
 
        Mockito.when(subscriptionRepository.findAll()).thenReturn(subscriptionList);
        
		webClient.get().uri("/subscription")
        	.header(HttpHeaders.ACCEPT, "application/json")
        	.exchange()
        	.expectStatus().isOk()
        	.expectBodyList(SubscriptionDTO.class);
		
		Mockito.verify(subscriptionRepository, times(1)).findAll();
	}
	
	@Test
	public void testGetSubscription() {
        Mockito.when(subscriptionRepository.findBySubscriptionId(SUBSCRIPTION_ID)).thenReturn(generateSubscription());
        
		webClient.get().uri("/subscription/"+ SUBSCRIPTION_ID)
        	.header(HttpHeaders.ACCEPT, "application/json")
        	.exchange()
        	.expectStatus().isOk()
        	.expectBody()
        	.jsonPath("$.subscriptionId").isEqualTo(SUBSCRIPTION_ID);
		
		Mockito.verify(subscriptionRepository, times(1)).findBySubscriptionId(SUBSCRIPTION_ID);
	}
	
	
	@Test
	public void testGetSubscriptionAnyInSystem() {
		webClient.get().uri("/subscription/"+ SUBSCRIPTION_ID)
        	.header(HttpHeaders.ACCEPT, "application/json")
        	.exchange()
        	.expectStatus().isBadRequest();
		
	}
	
	@Test
	public void testCreateSubscription() {
		webClient.post()
			.uri("/subscription")
        	.header(HttpHeaders.ACCEPT, "application/json")
        	.body(Mono.just(generateSubscriptionDTO()), SubscriptionDTO.class)
        	.exchange()
        	.expectStatus().isCreated();
		
	}
	
	
	/* Private methods for the creation of test data */
	
	private Subscription generateSubscription() {
		Subscription subscription = new Subscription();
		subscription.setConsent(true);
		subscription.setDateOfBirth(LocalDate.now());
		subscription.setEmail("test@email.com");
		subscription.setFirstName("firstName");
		subscription.setGender(Gender.FEMALE);
		subscription.setNewsletterId(12L);
		subscription.setSubscriptionId(SUBSCRIPTION_ID);
		
		return subscription;
	}
	
	private SubscriptionDTO generateSubscriptionDTO() {
		SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
		subscriptionDTO.setConsent(true);
		subscriptionDTO.setDateOfBirth(LocalDate.now());
		subscriptionDTO.setEmail("test@email.com");
		subscriptionDTO.setFirstName("firstName");
		subscriptionDTO.setGender(Gender.FEMALE);
		subscriptionDTO.setNewsletterId(12L);
		subscriptionDTO.setSubscriptionId(SUBSCRIPTION_ID);
		
		return subscriptionDTO;
	}
	
}

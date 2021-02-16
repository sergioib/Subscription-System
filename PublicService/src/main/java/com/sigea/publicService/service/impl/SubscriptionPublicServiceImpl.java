package com.sigea.publicService.service.impl;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

import com.sigea.publicService.domain.Subscription;
import com.sigea.publicService.exception.PublicServiceException;
import com.sigea.publicService.exception.PublicServiceExceptionCode;
import com.sigea.publicService.exception.PublicServiceExceptionMsn;
import com.sigea.publicService.repository.SubscriptionRepository;
import com.sigea.publicService.service.SubscriptionPublicService;
import com.sigea.publicService.utils.SubscriptionPublicServiceConstants;
import com.sigea.publicService.web.dto.SubscriptionDTO;

import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

@Service
@Log4j2
public class SubscriptionPublicServiceImpl implements SubscriptionPublicService{
	
	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	@Value("${subscriptionService.url}")
	private String subscriptionServiceURL;
	
	@Value("${subscriptionService.path}")
	private String subscriptionServicePath;
	
	@Value("${subscriptionService.security.userName}")
	private String userName;
	
	@Value("${subscriptionService.security.password}")
	private String password;
	
	@Autowired
	ModelMapper modelMapper;

	/**
	 * Create a new subscription in the system
	 * @param subscription
	 * @return
	 * @throws PublicServiceException
	 */
	public String createNewSubscription(Subscription subscription) throws PublicServiceException {
		
		// Generation of subscriptionId
		String subscriptionId = SubscriptionPublicServiceConstants.NODE_ID +"-"+ Instant.now().toEpochMilli();
		log.info("SubscriptionId generated: {}",subscriptionId);
		subscription.setSubscriptionId(subscriptionId);
		 
		WebClient webClient = WebClient.builder()
				.baseUrl(subscriptionServiceURL)
				.filter(ExchangeFilterFunctions.basicAuthentication(userName, password))
				.build();
		
		Mono<SubscriptionDTO> subscriptionMono = webClient
			      .post()
			      .uri(subscriptionServicePath)
			      .body(Mono.just(modelMapper.map(subscription, SubscriptionDTO.class)), SubscriptionDTO.class)
			      .retrieve()
			      .onStatus(HttpStatus::isError, response -> Mono.empty())
			      .bodyToMono(SubscriptionDTO.class);
		subscriptionMono.subscribe(s -> {
			log.info("Subscription register finished, {}", s);
			}, error -> {
				log.error(error);
			});
			   
		return subscriptionId;
	}
	
	/**
	 * Remove the subscription with the subscriptionId send as parameter
	 * @param subscriptionId
	 * @throws PublicServiceException
	 */
	public void cancelSubscription(String subscriptionId) throws PublicServiceException {
		log.info("BEGIN cancelSubscription()");
		log.info("Params: ");
		log.info("- supscriptionId: {}", subscriptionId);
		
		try {
			Subscription subscription = subscriptionRepository.findBySubscriptionId(subscriptionId);
			if(subscription != null) {
				subscriptionRepository.deleteById(subscriptionId);
			} else {
				log.error("Subcription {} not found", subscriptionId);
				throw new PublicServiceException(PublicServiceExceptionCode.SUBSCRIPTION_NOT_EXIST, PublicServiceExceptionMsn.SUBSCRIPTION_NOT_EXIST);
			}
			
		} catch(PublicServiceException e) {
			throw e;
		}
		catch(Exception e1) {
			log.error("Error quering datase", e1);
			throw new PublicServiceException(PublicServiceExceptionCode.DATABASE_ERRROR, PublicServiceExceptionMsn.DATABASE_ERRROR, e1);
		}
	}
	

	/**
	 * Get the subscription with the subscriptionId that has been sent as parameter
	 * @param subscriptionId
	 * @return
	 * @throws PublicServiceException
	 */
	public Subscription getSubscription(String subscriptionId) throws PublicServiceException {
		log.info("BEGIN getSubscription()");
		log.info("Params: ");
		log.info("- supscriptionId: {}", subscriptionId);
		
		try {
			 Subscription subscription = subscriptionRepository.findBySubscriptionId(subscriptionId);
			 
			 if(subscription == null) {
				 log.error("Subscription not found");
				 throw new PublicServiceException(PublicServiceExceptionCode.SUBSCRIPTION_NOT_EXIST, PublicServiceExceptionMsn.SUBSCRIPTION_NOT_EXIST);
			 }
			 
			 return subscription;
			 
		}  catch(PublicServiceException e) {
			throw e;
		} catch(Exception e1) {
			log.error("Error quering datase", e1);
			throw new PublicServiceException(PublicServiceExceptionCode.DATABASE_ERRROR, PublicServiceExceptionMsn.DATABASE_ERRROR, e1);
		}
	}

	/**
	 * Get all subscription of the system
	 * @return
	 * @throws PublicServiceException
	 */
	public List<Subscription> getAllSubscription() throws PublicServiceException {
		log.info("BEGIN getAllSubscription()");
		
		try {
			return subscriptionRepository.findAll();
		} catch(Exception e) {
			log.error("Error quering datase", e);
			throw new PublicServiceException(PublicServiceExceptionCode.DATABASE_ERRROR, PublicServiceExceptionMsn.DATABASE_ERRROR, e);
		}
	}

}

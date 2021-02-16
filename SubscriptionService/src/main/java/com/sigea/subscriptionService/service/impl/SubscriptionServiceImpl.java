package com.sigea.subscriptionService.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigea.subscriptionService.domain.Subscription;
import com.sigea.subscriptionService.exception.SubscriptionServiceException;
import com.sigea.subscriptionService.exception.SubscriptionServiceExceptionCode;
import com.sigea.subscriptionService.exception.SubscriptionServiceExceptionMsn;
import com.sigea.subscriptionService.repository.SubscriptionRepository;
import com.sigea.subscriptionService.service.SenderNotificationService;
import com.sigea.subscriptionService.service.SubscriptionService;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SubscriptionServiceImpl implements SubscriptionService {
	
	@Autowired
	SubscriptionRepository subscriptionRepository;
	
	@Autowired
	SenderNotificationService senderNotificationService;
	
	/**
	 * It has the business logic to create a new subscription in the system and send 
	 * a notification to notify that the subscription has been created 
	 * @param subscription
	 * @return the if of the created subscription
	 * @throws SubscriptionServiceException
	 */
	public String createNewSubscription(Subscription subscription) throws SubscriptionServiceException {
		log.info("BEGIN createNewSubscription()");
		log.info("PARAMS: ");
		log.info("- subscription: {}", subscription);
		
		try {
			// check if the subscription exist
			List<Subscription> subscriptionList = subscriptionRepository.findBySubscriptionIdOrEmail(subscription.getSubscriptionId(), subscription.getEmail());
			
			if(subscriptionList != null && subscriptionList.size() > 0) {
				log.error("It already exist a subcripction with the same data");
				throw new SubscriptionServiceException(SubscriptionServiceExceptionCode.SUBSCRIPTION_ALLREADY_EXIST, SubscriptionServiceExceptionMsn.SUBSCRIPTION_ALLREADY_EXIST);
			} 
			
			// it is a new subscription
			Subscription newSubscription = subscriptionRepository.saveAndFlush(subscription);
			
			// call to email notification microservice
			senderNotificationService.sendNotification(newSubscription);
			
			return newSubscription.getSubscriptionId();
		} catch (SubscriptionServiceException e) {
			throw e;
		} catch(Exception e1) {
			log.error("Unexpedted error");
			throw new SubscriptionServiceException(SubscriptionServiceExceptionCode.UNEXPECTED_ERROR, SubscriptionServiceExceptionMsn.UNEXPECTED_ERROR, e1);
		}
		
	}

}

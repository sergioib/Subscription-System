package com.sigea.subscriptionService.service;

import com.sigea.subscriptionService.domain.Subscription;
import com.sigea.subscriptionService.exception.SubscriptionServiceException;

public interface SubscriptionService {

	/**
	 * It has the business logic to create a new subscription in the system and send 
	 * a notification to notify that the subscription has been created 
	 * @param subscription
	 * @return
	 * @throws SubscriptionServiceException
	 */
	public String createNewSubscription(Subscription subscription) throws SubscriptionServiceException;
}

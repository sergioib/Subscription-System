package com.sigea.publicService.service;

import java.util.List;

import com.sigea.publicService.domain.Subscription;
import com.sigea.publicService.exception.PublicServiceException;

public interface SubscriptionPublicService {
	
	/**
	 * Create a new subscription in the system
	 * @param subscription
	 * @return
	 * @throws PublicServiceException
	 */
	public String createNewSubscription(Subscription subscription) throws PublicServiceException;
	
	/**
	 * Remove the subscription with the subscriptionId send as parameter
	 * @param subscriptionId
	 * @throws PublicServiceException
	 */
	public void cancelSubscription(String subscriptionId) throws PublicServiceException;
	
	/**
	 * Get the subscription with the subscriptionId that has been sent as parameter
	 * @param subscriptionId
	 * @return
	 * @throws PublicServiceException
	 */
	public Subscription getSubscription(String subscriptionId) throws PublicServiceException;
	
	/**
	 * Get all subscription of the system
	 * @return
	 * @throws PublicServiceException
	 */
	public List<Subscription> getAllSubscription() throws PublicServiceException;

}

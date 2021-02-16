package com.sigea.publicService.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sigea.publicService.domain.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String>{
	
	/**
	 * Get from database the subscription with id subscriptionId
	 * @param subscriptionId
	 * @return
	 */
	public Subscription findBySubscriptionId(String subscriptionId);
	
}

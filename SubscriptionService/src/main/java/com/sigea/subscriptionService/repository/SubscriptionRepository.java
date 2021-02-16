package com.sigea.subscriptionService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sigea.subscriptionService.domain.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, String>{
	
	/**
	 * Return all the subscription of the system that has the subscriptionId or the email 
	 * that has been sent as a parameters
	 * @param subscriptionId
	 * @param email
	 * @return
	 */
	public List<Subscription> findBySubscriptionIdOrEmail(String subscriptionId, String email);

}

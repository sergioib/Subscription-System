package com.sigea.emailService.service;

import com.sigea.emailService.web.dto.SubscriptionDTO;

public interface ReceiverNotificationService {

	/**
	 * Method which is executed for processing the messages of the queue
	 * @param subscriptionDTO
	 */
	public void recievedMessage(SubscriptionDTO subscriptionDTO);
}

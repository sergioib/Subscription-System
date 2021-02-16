package com.sigea.emailService.service;

import com.sigea.emailService.web.dto.SubscriptionDTO;

public interface EmailService {

	/**
	 * This method send a mail with the data of the subscription
	 * @param subcriptionDTO
	 */
	public void sendMail(SubscriptionDTO subcriptionDTO);
}

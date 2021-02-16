package com.sigea.subscriptionService.service;

import com.sigea.subscriptionService.domain.Subscription;

public interface SenderNotificationService {

	/**
	 * Send a notification to notify that a new subscription has been created
	 * @param subscription
	 */
	public void sendNotification(Subscription subscription);
}

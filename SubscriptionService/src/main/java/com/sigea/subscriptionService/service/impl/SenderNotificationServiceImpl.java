package com.sigea.subscriptionService.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigea.subscriptionService.domain.Subscription;
import com.sigea.subscriptionService.service.SenderNotificationService;
import com.sigea.subscriptionService.web.dto.SubscriptionDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SenderNotificationServiceImpl implements SenderNotificationService {
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Autowired
	private Queue queue;
	
	@Autowired
	ModelMapper modelMapper;
	
	/**
	 * It sends a message to a queue to notify that a subscription has been created
	 * @param Subscription
	 */
	public void sendNotification(Subscription subscription) {
		log.info("BEGIN sendNotification()");
		
		try {
			rabbitTemplate.convertAndSend(queue.getName(), modelMapper.map(subscription, SubscriptionDTO.class));
		} catch(Exception e) {
			log.error("Error sending message to notification queue");
		}
		
		log.info("The notification has been correctly sent");
		
	}
}

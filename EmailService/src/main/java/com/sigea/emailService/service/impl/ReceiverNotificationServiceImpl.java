package com.sigea.emailService.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sigea.emailService.service.EmailService;
import com.sigea.emailService.service.ReceiverNotificationService;
import com.sigea.emailService.web.dto.SubscriptionDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ReceiverNotificationServiceImpl implements ReceiverNotificationService{
	
	@Autowired
	private EmailService emailService;

	/**
	 * Method which is executed for processing the messages of the queue
	 * @param subscriptionDTO
	 */
	@RabbitListener(queues = "${emailService.rabbitmq.queue}")
	public void recievedMessage(SubscriptionDTO subscriptionDTO) {
		log.info("Menssage received, {}",subscriptionDTO);
		
		emailService.sendMail(subscriptionDTO);
		
		log.info("Menssage sent");
		
	}

}

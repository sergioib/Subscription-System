package com.sigea.emailService.service.impl;

import org.springframework.stereotype.Service;

import com.sigea.emailService.service.EmailService;
import com.sigea.emailService.web.dto.SubscriptionDTO;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EmailServiceImpl implements EmailService{
	
	/**
	 * This method send a mail with the data of the subscription
	 * @param subcriptionDTO
	 */
	public void sendMail(SubscriptionDTO subcriptionDTO) {
		log.info("BEGIN sendMail()");
		try {
			Thread.sleep(2000);
			log.info("The email has been sent correctly");
		} catch(Exception e) {
			log.error("It has been an error sending the email",e);
		}
		
	}

	
}

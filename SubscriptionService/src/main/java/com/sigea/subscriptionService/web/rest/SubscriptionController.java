package com.sigea.subscriptionService.web.rest;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigea.subscriptionService.domain.Subscription;
import com.sigea.subscriptionService.exception.SubscriptionServiceException;
import com.sigea.subscriptionService.exception.SubscriptionServiceExceptionCode;
import com.sigea.subscriptionService.exception.SubscriptionServiceExceptionMsn;
import com.sigea.subscriptionService.service.SubscriptionService;
import com.sigea.subscriptionService.web.dto.ErrorDTO;
import com.sigea.subscriptionService.web.dto.SubscriptionDTO;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/subscription")
@Slf4j
public class SubscriptionController {
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Method which has the logic necessary for the creation of a new subscription
	 * @param subscriptionDTO -> data of the subscription that has to be created
	 * @return
	 * 		subscriptionDTO, If the subscription is correctly created, return 201-Created with the data of the subscription created
	 * 		ErrorDTO, If coming parameters are incorrect, return 400-Bad Request and the cause of the error 
	 * 		ErrorDTO, If there are some unexpected error, return 500-Internal Server Error
	 */
	@PostMapping("")
	public ResponseEntity<?> createSubscription(@RequestBody SubscriptionDTO subscriptionDTO){
		log.info("BEGIN createSubscription()");
		log.info("Params:");
		log.info("- subscriptionDTO: {}", subscriptionDTO);
		
		try {
			valitateParams(subscriptionDTO);
		} catch (SubscriptionServiceException e) {
			log.error("Unexpected error", e);
			ErrorDTO errorDTO = new ErrorDTO(SubscriptionServiceExceptionCode.UNEXPECTED_ERROR, SubscriptionServiceExceptionMsn.UNEXPECTED_ERROR);
			return new ResponseEntity<ErrorDTO>(errorDTO,HttpStatus.BAD_REQUEST);
		}
		
		try {
			subscriptionService.createNewSubscription(modelMapper.map(subscriptionDTO, Subscription.class));
			return new ResponseEntity<SubscriptionDTO>(subscriptionDTO, HttpStatus.CREATED);
			
		} catch(SubscriptionServiceException e) {
			ErrorDTO errorDTO = new ErrorDTO(e.getCode(), e.getMsn());
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Throwable e1) {
			log.error("Unexpected error",e1);
			ErrorDTO errorDTO = new ErrorDTO(SubscriptionServiceExceptionCode.UNEXPECTED_ERROR, SubscriptionServiceExceptionMsn.UNEXPECTED_ERROR);
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/* ------ Private methods ------- */
	
	/**
	 * Validate if all parameters have correct values
	 * @param subscriptionDTO
	 * @throws SubscriptionServiceException
	 */
	private void valitateParams(SubscriptionDTO subscriptionDTO) throws SubscriptionServiceException {
		log.info("BEGIN validateParams()");
		
		if(subscriptionDTO.getSubscriptionId() == null || subscriptionDTO.getSubscriptionId().trim().isEmpty()) {
			log.error("SubscriptionId is null or empty");
			throw new SubscriptionServiceException(SubscriptionServiceExceptionCode.SUBSCRIPTIONID_COMPULSORY_ERROR, SubscriptionServiceExceptionMsn.SUBSCRIPTIONID_COMPULSORY_ERROR);
		} else if(subscriptionDTO.getEmail() == null || subscriptionDTO.getEmail().trim().isEmpty()) {
			log.error("Email is null or empty");
			throw new SubscriptionServiceException(SubscriptionServiceExceptionCode.EMAIL_COMPULSORY_ERROR, SubscriptionServiceExceptionMsn.EMAIL_COMPULSORY_ERROR);
		} else if(subscriptionDTO.getDateOfBirth() == null) {
			log.error("DateOfBirth is null");
			throw new SubscriptionServiceException(SubscriptionServiceExceptionCode.DATE_OF_BIRTH_COMPULSORY_ERROR, SubscriptionServiceExceptionMsn.DATE_OF_BIRTH_COMPULSORY_ERROR);
		} else if(subscriptionDTO.getNewsletterId() == null) {
			log.error("Newsletter is null");
			throw new SubscriptionServiceException(SubscriptionServiceExceptionCode.NEWSLETTER_COMPULSORY_ERROR, SubscriptionServiceExceptionMsn.NEWSLETTER_COMPULSORY_ERROR);
		}

	}
}

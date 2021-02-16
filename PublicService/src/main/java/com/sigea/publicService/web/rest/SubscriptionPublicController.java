package com.sigea.publicService.web.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sigea.publicService.domain.Subscription;
import com.sigea.publicService.exception.PublicServiceException;
import com.sigea.publicService.exception.PublicServiceExceptionCode;
import com.sigea.publicService.exception.PublicServiceExceptionMsn;
import com.sigea.publicService.service.SubscriptionPublicService;
import com.sigea.publicService.web.dto.ErrorDTO;
import com.sigea.publicService.web.dto.SubscriptionDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/subscription")
@Log4j2
@Tag(name = "Subscription", description = "The subscription API")
public class SubscriptionPublicController {
	
	@Autowired
	private SubscriptionPublicService subscriptionPublicService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	/**
	 * Entry point to create a new subscription in the system.
	 * @param subscriptionDTO
	 * @return the subscriptionId of the subscription
	 */
	@PostMapping("")
	@Operation(summary = "Create a new subscription", description = "Create a new subscription")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Successful operation", 
	                content = @Content(schema = @Schema(implementation = String.class))) })	
	public ResponseEntity<?> createSubscrition(@RequestBody SubscriptionDTO subscriptionDTO) {
		log.info("BEGIN createSubscrition()");
		log.info("Params: ");
		log.info("- subscriptionDTO: {}", subscriptionDTO);
		// validate input params
		try {
			validateParams(subscriptionDTO);
		} catch (PublicServiceException e){
			log.error("Compulsary params are null or empty");
			ErrorDTO errorDTO = new ErrorDTO(e.getCode(),e.getMsn());
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.BAD_REQUEST);
		}
		
		// call to SubscriptionSPublicService
		try {
			String subscriptionId = subscriptionPublicService.createNewSubscription(modelMapper.map(subscriptionDTO, Subscription.class));

			return new ResponseEntity<String>(subscriptionId, HttpStatus.CREATED);
			
		} catch(PublicServiceException e) {
			ErrorDTO errorDTO = new ErrorDTO(e.getCode(), e.getMsn());
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Throwable e1) {
			log.error("Unexpected error",e1);
			ErrorDTO errorDTO = new ErrorDTO(PublicServiceExceptionCode.UNEXPECTED_ERROR, PublicServiceExceptionMsn.UNEXPECTED_ERROR);
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * Entry point to delete a subscription of the system
	 * @param subscriptionDTO
	 * @return
	 * 		204 - No Content, If the subscription has been correctly removed
	 * 		ErrorDTO, 400 - Bad Request, If the subsctiptionId of the entry parameter does not exist in the system
	 * 		ErrorDTO, 500 - Internal Server Error, If exist some error in the database
	 */
	@DeleteMapping("/{subscriptionId}")
	@Operation(summary = "Delete a subscription", description = "Delete a subscription which exists in the system")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "204", description = "Successful operation"),
	        @ApiResponse(responseCode = "400", description = "The subcription does not exist in the system",
	        		content = @Content(schema= @Schema(implementation = ErrorDTO.class))) })	
	public ResponseEntity<?> removeSubscrition(
			@Parameter(description="Id of the subscription to be delete. Cannot be empty.", required=true)
			@PathVariable String subscriptionId) {
		log.info("BEGIN removeSubscrition()");
		log.info("Params: ");
		log.info("- subscriptionId: {}", subscriptionId);
		
		try {
			subscriptionPublicService.cancelSubscription(subscriptionId);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			
		} catch(PublicServiceException e) {
			ErrorDTO errorDTO = new ErrorDTO(e.getCode(), e.getMsn());
			if(e.getCode().equals(PublicServiceExceptionCode.SUBSCRIPTION_NOT_EXIST)) {
				return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Throwable e1) {
			log.error("Unexpected error",e1);
			ErrorDTO errorDTO = new ErrorDTO(PublicServiceExceptionCode.UNEXPECTED_ERROR, PublicServiceExceptionMsn.UNEXPECTED_ERROR);
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Entry point to get a subscription of the system
	 * @param subscriptionDTO
	 * @return
	 * 		SubscriptionDTO, 200 - OK, If the subscription exist in the system
	 * 		ErrorDTO, 400 - Bad Request, If the subsctiptionId of the entry parameter does not exist in the system
	 * 		ErrorDTO, 500 - Internal Server Error, If exist some error in the database
	 */
	@GetMapping("/{subscriptionId}")
	@Operation(summary = "Get one subscription", description = "Get the subscription if exist in the system")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Successful operation",
	        		content = @Content(schema= @Schema(implementation = SubscriptionDTO.class))),
	        @ApiResponse(responseCode = "400", description = "The subcription does not exist in the system",
	        		content = @Content(schema= @Schema(implementation = ErrorDTO.class))) })	
	public ResponseEntity<?> getSubscription(
			@Parameter(description="Id of the subscription to be return. Cannot be empty.", required=true)
			@PathVariable String subscriptionId){
		log.info("BEGIN getSubscription()");
		log.info("Params: ");
		log.info("- subscriptionId: {}", subscriptionId);
		try {
			Subscription subscription = subscriptionPublicService.getSubscription(subscriptionId);
			
			return new ResponseEntity<SubscriptionDTO>(modelMapper.map(subscription, SubscriptionDTO.class), HttpStatus.OK);
		} catch(PublicServiceException e) {
			ErrorDTO errorDTO = new ErrorDTO(e.getCode(), e.getMsn());
			if(e.getCode().equals(PublicServiceExceptionCode.SUBSCRIPTION_NOT_EXIST)) {
				return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch(Throwable e1) {
			log.error("Unexpected error",e1);
			ErrorDTO errorDTO = new ErrorDTO(PublicServiceExceptionCode.UNEXPECTED_ERROR, PublicServiceExceptionMsn.UNEXPECTED_ERROR);
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	/**
	 * Entry point to get all subscriptions of the system
	 * @param subscriptionDTO
	 * @return
	 * 		SubscriptionDTO list, 200 - OK, with all the subscription of the system
	 */
	@GetMapping("")
	@Operation(summary = "Get all subscriptions", description = "Get all subscriptions of the system")
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "Successful operation",
	        		content = @Content(array = @ArraySchema(schema= @Schema(implementation = SubscriptionDTO.class))))})
	public ResponseEntity<?> getAllSubscription(){
		log.info("BEGIN getAllSubscription()");
		
		try {
			List<Subscription> subscriptionList = subscriptionPublicService.getAllSubscription();
			
			if(subscriptionList == null || subscriptionList.size() == 0) {
				log.info("There are not subscriptions in the system");
			}
			List<SubscriptionDTO> subscriptionDTOList = subscriptionList.stream()
	          .map(subscription -> modelMapper.map(subscription, SubscriptionDTO.class))
	          .collect(Collectors.toList());
			
			return new ResponseEntity<List<SubscriptionDTO>>(subscriptionDTOList,HttpStatus.OK);
		} catch(Throwable e1) {
			log.error("Unexpected error",e1);
			ErrorDTO errorDTO = new ErrorDTO(PublicServiceExceptionCode.UNEXPECTED_ERROR, PublicServiceExceptionMsn.UNEXPECTED_ERROR);
			return new ResponseEntity<ErrorDTO>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
/* ------ Private methods ------- */
	
	/**
	 * Validate if all parameters have correct values
	 * @param subscriptionDTO
	 * @throws PublicServiceException
	 */
	private void validateParams(SubscriptionDTO subscriptionDTO) throws PublicServiceException{
		log.info("BEGIN validateParams()");
		
		if(subscriptionDTO.getEmail() == null || subscriptionDTO.getEmail().trim().isEmpty()) {
			log.error("Email is null or empty");
			throw new PublicServiceException(PublicServiceExceptionCode.EMAIL_COMPULSORY_ERROR, PublicServiceExceptionMsn.EMAIL_COMPULSORY_ERROR);
		} else if(subscriptionDTO.getDateOfBirth() == null) {
			log.error("DateOfBirth is null");
			throw new PublicServiceException(PublicServiceExceptionCode.DATE_OF_BIRTH_COMPULSORY_ERROR, PublicServiceExceptionMsn.DATE_OF_BIRTH_COMPULSORY_ERROR);
		} else if(subscriptionDTO.getNewsletterId() == null) {
			log.error("Newsletter is null");
			throw new PublicServiceException(PublicServiceExceptionCode.NEWSLETTER_COMPULSORY_ERROR, PublicServiceExceptionMsn.NEWSLETTER_COMPULSORY_ERROR);
		}

	}
	
}

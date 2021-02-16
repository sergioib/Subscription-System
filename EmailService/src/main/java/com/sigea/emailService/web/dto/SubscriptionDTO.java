package com.sigea.emailService.web.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigea.emailService.domain.enums.Gender;

import lombok.Data;

@Data
public class SubscriptionDTO {

	private String subscriptionId;
	
	private String email;
	
	private String firstName;
	
	private Gender gender;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private boolean consent;
	
	private Long newsletterId;
	
}

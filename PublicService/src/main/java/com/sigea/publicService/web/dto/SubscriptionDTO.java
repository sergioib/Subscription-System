package com.sigea.publicService.web.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigea.publicService.domain.enums.Gender;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SubscriptionDTO {

	private String subscriptionId;
	
	@Schema(required = true)
	private String email;
	
	private String firstName;
	
	private Gender gender;
	
	@JsonFormat(pattern="yyyy-MM-dd")
	@Schema(required = true)
	private LocalDate dateOfBirth;
	
	@Schema(required = true)
	private boolean consent;
	
	@Schema(required = true)
	private Long newsletterId;
	
}

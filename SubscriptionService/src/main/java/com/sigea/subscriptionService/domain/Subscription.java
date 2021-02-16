package com.sigea.subscriptionService.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import com.sigea.subscriptionService.domain.enums.Gender;

import lombok.Data;

@Entity
@Data
public class Subscription {
	
	@Id
	private String subscriptionId;
	
	@Column(nullable = false)
	private String email;
	
	private String firstName;
	
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	@Column(nullable = false)
	private LocalDate dateOfBirth;
	
	@Column(nullable = false)
	private Boolean consent;
	
	@Column(nullable = false)
	private Long newsletterId;
	
}

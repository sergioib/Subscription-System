package com.sigea.subscriptionService.exception;

import lombok.Getter;

@SuppressWarnings("serial")
public class SubscriptionServiceException extends Exception {

	@Getter
	private String code;
	@Getter
	private String msn;
	
	public SubscriptionServiceException(String code, String msn) {
		this.code = code;
		this.msn = msn;
	}
	
	public SubscriptionServiceException(String code, String msn, Exception e) {
		super(e);
		this.code = code;
		this.msn = msn;
	}
	
}

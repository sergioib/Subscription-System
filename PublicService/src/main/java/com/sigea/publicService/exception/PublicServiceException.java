package com.sigea.publicService.exception;

import lombok.Getter;

public class PublicServiceException extends Exception {

	@Getter
	private String code;
	@Getter
	private String msn;
	
	public PublicServiceException(String code, String msn) {
		this.code = code;
		this.msn = msn;
	}
	
	public PublicServiceException(String code, String msn, Exception e) {
		super(e);
		this.code = code;
		this.msn = msn;
	}
	
}

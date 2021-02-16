package com.sigea.publicService.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {
	
	public String errorCode;
	public String errorMsn;

}

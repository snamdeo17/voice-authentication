package com.mastercard.authentication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value= VoiceMasterException.class)
	public ResponseEntity<TraceableError> handleVoiceMasterException(VoiceMasterException exception){
		if(null == exception.getStatusCode()) {
			exception.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		TraceableError error = new TraceableError();
		error.setErrorCode(exception.getStatusCode().toString());
		error.setMessage(exception.getMessage());
		
		return new ResponseEntity<>(error, exception.getStatusCode());
	}
	
}
